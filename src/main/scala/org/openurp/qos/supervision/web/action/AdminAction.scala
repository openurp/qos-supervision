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
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.view.View
import org.beangle.webmvc.support.helper.QueryHelper
import org.openurp.base.model.{Project, Semester}
import org.openurp.edu.schedule.service.LessonSchedule
import org.openurp.qos.supervision.model.*

import java.time.LocalTime

class AdminAction extends DepartAction {
  protected override def getLevels(): Seq[SupervisingLevel] = {
    entityDao.getAll(classOf[SupervisingLevel])
  }

  override def editSetting(supervision: Supervision): Unit = {
    val clazz = supervision.clazz
    put("assessor", supervision.assessor)
    put("clazz", supervision.clazz)

    val beginAt = clazz.semester.beginOn.atTime(LocalTime.MIN)
    val endAt = clazz.semester.endOn.atTime(LocalTime.MAX)
    val units = Collections.newSet[Int]
    clazz.schedule.activities foreach { activity =>
      (activity.beginUnit.toInt to activity.endUnit.toInt) foreach { u =>
        units.addOne(u)
      }
    }
    put("units", units.toList.sorted)
    val schedules = LessonSchedule.convert(clazz.schedule.activities, beginAt, endAt).sorted
    put("schedules", schedules)
    put("supervisionForm", supervisionService.getSupervisionForm(clazz.semester))

    super.editSetting(supervision)
  }

  override def inputSearch(): View = {
    given project: Project = getProject

    val semester = entityDao.get(classOf[Semester], getInt("supervision.clazz.semester.id", 0))
    put("semester", semester)
    val supervisors = supervisionService.getSupervisors(semester, null)
    put("supervisors", supervisors)
    forward()
  }

  override protected def getQueryBuilder: OqlBuilder[Supervision] = {
    val alias = simpleEntityName
    val builder = OqlBuilder.from(entityClass, alias)
    populateConditions(builder)
    QueryHelper.sort(builder)
    get("teacherName") foreach { teacherName =>
      builder.where("exists(from supervision.clazz.teachers as t where t.name like :teacherName)", "%" + teacherName + "%")
    }
    builder.tailOrder(alias + ".id")
    builder.limit(getPageLimit)
  }
}
