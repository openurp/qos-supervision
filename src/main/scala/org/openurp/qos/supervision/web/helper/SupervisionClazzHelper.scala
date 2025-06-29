/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.qos.supervision.web.helper

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.commons.lang.time.WeekDay
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.security.Securities
import org.beangle.webmvc.context.Params
import org.beangle.webmvc.support.helper.QueryHelper
import org.openurp.base.model.Department
import org.openurp.edu.clazz.model.Clazz
import org.openurp.qos.supervision.model.{Supervision, SupervisionClazz, SupervisionClazzCategory, Supervisor}

class SupervisionClazzHelper(entityDao: EntityDao) {

  def buildQuery(supervisor: Option[Supervisor]): OqlBuilder[Clazz] = {
    val query = OqlBuilder.from(classOf[Clazz])
    supervisor foreach { su =>
      if (su.level.name.contains("院")) {
        val departs = Collections.newBuffer[Department]
        departs.addOne(su.user.department)
        departs.addAll(su.user.department.children)
        query.where("clazz.teachDepart in(:departs)", departs)
      }
    }
    QueryHelper.populate(query)
    //query.where("size(clazz.schedule.activities)>0")
    //query.where("size(clazz.teachers)>0")
    val weekDay = Params.getInt("clazzActivity.time.week")
    val unit = Params.getInt("clazzActivity.beginUnit")
    if (weekDay.nonEmpty || unit.nonEmpty) {
      val activityQuery = new StringBuilder("exists( from clazz.schedule.activities as activity where 1=1 ")
      if (weekDay.nonEmpty) {
        val wd = WeekDay.of(weekDay.get)
        activityQuery.append(" and to_char(activity.time.startOn,'D')='" + wd.index + "'")
      }
      if (unit.nonEmpty) {
        activityQuery.append(" and activity.beginUnit=" + unit.get)
      }
      activityQuery.append(")")
      query.where(activityQuery.toString())
    }
    Params.getInt("category.id") foreach { categoryId =>
      val category = entityDao.get(classOf[SupervisionClazzCategory], categoryId)
      query.where("exists(from " + classOf[SupervisionClazz].getName + " at where  :category in elements(at.categories) and at.clazz=clazz)", category)
    }
    Params.get("teacher.name") foreach { teacherName =>
      if (Strings.isNotBlank(teacherName)) {
        query.where("exists(from clazz.teachers t where t.name like :teacherName)", "%" + teacherName.trim + "%")
      }
    }
    Params.get("course.name") foreach { courseName =>
      if (Strings.isNotBlank(courseName)) {
        query.where("clazz.course.name like :courseName", "%" + courseName.trim + "%")
      }
    }
    Params.get("supervising").getOrElse("all") match {
      case "me" =>
        val me = Securities.user
        query.where("exists(from " + classOf[Supervision].getName + " a where a.clazz=clazz and a.assessor.code=:me)", me)
      case "other" => query.where("exists(from " + classOf[Supervision].getName + " a where a.clazz=clazz)")
      case "none" => query.where("not exists(from " + classOf[Supervision].getName + " a where a.clazz=clazz)")
      case _ =>
    }
    query
  }

}
