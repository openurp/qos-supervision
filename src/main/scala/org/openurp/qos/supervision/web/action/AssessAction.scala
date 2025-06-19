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
import org.beangle.commons.lang.time.WeekDay
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.view.View
import org.beangle.webmvc.support.helper.QueryHelper
import org.openurp.base.hr.model.Teacher
import org.openurp.base.model.{Project, Semester}
import org.openurp.edu.clazz.model.Clazz
import org.openurp.edu.schedule.service.{LessonSchedule, ScheduleDigestor}
import org.openurp.qos.supervision.model.*
import org.openurp.qos.supervision.service.SupervisionService
import org.openurp.qos.supervision.web.helper.{SupervisionClazzHelper, SupervisionHelper}
import org.openurp.starter.web.support.TeacherSupport

import java.time.{LocalDate, LocalTime}

class AssessAction extends TeacherSupport {

  var supervisionService: SupervisionService = _

  protected override def projectIndex(teacher: Teacher)(using project: Project): View = {
    val semester = getSemester
    put("semester", semester)
    put("weekdays", WeekDay.values)
    supervisionService.getSupervisor(semester, getUser) match
      case Some(supervisor) =>
        put("supervisor", supervisor)
        val builder = OqlBuilder.from(classOf[SupervisionClazzCategory], "c")
        builder.where("exists(from " + classOf[SupervisionClazz].getName +
          " t join t.categories as tc where tc=c and t.semester=:semester)", semester)
        builder.orderBy("c.code")
        put("categories", entityDao.search(builder))
        forward()
      case None =>
        forward("not-supervisor")
  }

  def search(): View = {
    val semester = entityDao.get(classOf[Semester], getIntId("clazz.semester"))
    val supervisor = supervisionService.getSupervisor(semester, getUser)
    val query = new SupervisionClazzHelper(entityDao).buildQuery(supervisor)
    query.limit(QueryHelper.pageLimit)
    val clazzes = entityDao.search(query)
    put("clazzes", clazzes)
    val activities = clazzes.map { clazz => (clazz.id.toString, ScheduleDigestor.digest(clazz, ":day :units :weeks")) }.toMap
    put("activities", activities)
    val rooms = clazzes.map { clazz => (clazz.id.toString, clazz.schedule.activities.map(_.rooms.map(_.name).mkString(",")).toSet.mkString(",")) }.toMap
    put("rooms", rooms)

    val assessedQuery = OqlBuilder.from(classOf[Supervision].getName, "a")
    assessedQuery.where("a.clazz.semester=:semester", semester)
    assessedQuery.where("a.assessor=:me", getUser)
    assessedQuery.select("a.clazz.id")
    val taskIds = entityDao.search(assessedQuery)
    put("taskIds", taskIds.toSet)
    forward()
  }

  def edit(): View = {
    val clazz = entityDao.get(classOf[Clazz], getLongId("clazz"))
    put("clazz", clazz)

    val units = Collections.newSet[Int]
    clazz.schedule.activities foreach { activity =>
      (activity.beginUnit.toInt to activity.endUnit.toInt) foreach { u =>
        units.addOne(u)
      }
    }
    put("units", units.toList.sorted)

    val beginAt = clazz.semester.beginOn.atTime(LocalTime.MIN)
    val endAt = clazz.semester.endOn.atTime(LocalTime.MAX)
    val schedules = LessonSchedule.convert(clazz.schedule.activities, beginAt, endAt).sorted
    put("schedules", schedules)
    put("supervisionForm", supervisionService.getSupervisionForm(clazz.semester))
    val me = getUser
    val supervision = new SupervisionHelper(entityDao).getOrCreate(clazz, me)
    put("supervision", supervision)
    put("assessor", me)
    forward()
  }

  def report(): View = {
    val clazz = entityDao.get(classOf[Clazz], getLongId("clazz"))
    val me = this.getUser
    new SupervisionHelper(entityDao).report(clazz, me)
    forward()
  }

  def save(): View = {
    val clazz = entityDao.get(classOf[Clazz], getLongId("clazz"))
    val me = this.getUser
    val supervision = new SupervisionHelper(entityDao).getOrCreate(clazz, me)
    val form = entityDao.get(classOf[SupervisionForm], getLongId("supervisionForm"))
    val supervisor = supervisionService.getSupervisor(clazz.semester, me)
    supervision.level = supervisor.get.level
    new SupervisionHelper(entityDao).save(form, supervision)
    redirect("report", "&clazz.id=" + clazz.id, "info.save.success")
  }

}
