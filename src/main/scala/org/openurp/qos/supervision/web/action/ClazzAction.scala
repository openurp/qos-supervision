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

package org.openurp.qos.supervision.web.action

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.openurp.base.model.{Project, Semester}
import org.openurp.edu.clazz.model.Clazz
import org.openurp.edu.schedule.service.ScheduleDigestor
import org.openurp.qos.supervision.model.{SupervisionClazz, SupervisionClazzCategory}
import org.openurp.starter.web.support.ProjectSupport

class ClazzAction extends RestfulAction[SupervisionClazz], ProjectSupport, ExportSupport[SupervisionClazz] {

  override protected def indexSetting(): Unit = {
    super.indexSetting()

    given project: Project = getProject

    put("project", project)
    put("departs", getDeparts)
    put("semester", getSemester)
    put("categories", codeService.get(classOf[SupervisionClazzCategory]))
  }

  def addSetting(): View = {
    put("categories", codeService.get(classOf[SupervisionClazzCategory]))
    val semester = entityDao.get(classOf[Semester], getIntId("supervisionClazz.semester"))
    put("semester", semester)
    forward()
  }

  override def search(): View = {
    val supervisionClazzes = entityDao.search(getQueryBuilder)
    put("supervisionClazzes", supervisionClazzes)
    val activities = supervisionClazzes.map { x => (x.clazz.id.toString, ScheduleDigestor.digest(x.clazz, ":day :units :weeks")) }.toMap
    put("activities", activities)
    val rooms = supervisionClazzes.map { x => (x.clazz.id.toString, x.clazz.schedule.activities.map(_.rooms.map(_.name).mkString(",")).toSet.mkString(",")) }.toMap
    put("rooms", rooms)
    forward()
  }

  override protected def getQueryBuilder: OqlBuilder[SupervisionClazz] = {
    val query = super.getQueryBuilder
    getInt("category.id") foreach { categoryId =>
      val category = entityDao.get(classOf[SupervisionClazzCategory], categoryId)
      query.where(":category in elements(supervisionClazz.categories)", category)
    }
    get("teacher.name") foreach { teacherName =>
      if (Strings.isNotBlank(teacherName)) {
        query.where("exists(from supervisionClazz.clazz.teachers t where t.name like :teacherName)", "%" + teacherName.trim + "%")
      }
    }
    query
  }

  def addCategory(): View = {
    val project = getProject
    val category = entityDao.get(classOf[SupervisionClazzCategory], getIntId("category"))
    val semester = entityDao.get(classOf[Semester], getIntId("supervisionClazz.semester"))
    var crnstr: String = get("crns", "")
    crnstr = Strings.replace(crnstr, "，", ",")
    crnstr = Strings.replace(crnstr, "\t", ",")
    crnstr = Strings.replace(crnstr, "\n", ",")
    val crns: Array[String] = Strings.split(crnstr)
    val query =
      OqlBuilder.from(classOf[Clazz], "clazz").where("clazz.semester = :semester", semester)
        .where("clazz.project = :project",project)
        .where("clazz.crn in(:crns)", crns)
    val clazzes = entityDao.search(query)
    val targets = Collections.newBuffer[SupervisionClazz]
    for (clazz <- clazzes) {
      val target =
        entityDao.findBy(classOf[SupervisionClazz], "clazz", clazz).headOption match
          case None =>
            val n = new SupervisionClazz
            n.clazz = clazz
            n.semester = clazz.semester
            n
          case Some(t) => t

      target.categories.addOne(category)
      targets.addOne(target)
    }
    entityDao.saveOrUpdate(targets)
    redirect("search", "info.save.success")
  }

  def removeCategory(): View = {
    val slist = entityDao.find(classOf[SupervisionClazz], getLongIds("supervisionClazz"))
    val category = entityDao.get(classOf[SupervisionClazzCategory], getIntId("category"))
    slist.foreach(x => x.categories.subtractOne(category))
    entityDao.saveOrUpdate(slist)
    entityDao.remove(slist.filter(_.categories.isEmpty))
    redirect("search", "info.remove.success")
  }

}
