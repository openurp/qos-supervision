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
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.webmvc.context.{ActionContext, Params}
import org.openurp.base.hr.model.Teacher
import org.openurp.base.model.User
import org.openurp.edu.clazz.model.Clazz
import org.openurp.edu.schedule.service.LessonSchedule
import org.openurp.qos.supervision.model.{Supervision, SupervisionForm}

import java.time.{Instant, LocalDate, LocalTime}

class SupervisionHelper(entityDao: EntityDao) {

  def save(form: SupervisionForm, supervision: Supervision): Unit = {
    val schedule = Params.get("schedule").getOrElse("")
    val date = LocalDate.parse(Strings.substringBefore(schedule, "_"))
    val clazz = supervision.clazz
    val beginAt = clazz.semester.beginOn.atTime(LocalTime.MIN)
    val endAt = clazz.semester.endOn.atTime(LocalTime.MAX)
    val schedules = LessonSchedule.convert(clazz.schedule.activities, beginAt, endAt).sorted
    schedules.find(x => x.date == date) foreach { schedule =>
      supervision.assessOn = schedule.date
      supervision.room = schedule.room
      Params.getInt("courseUnit") match
        case None => supervision.courseUnit = Strings.substringBefore(schedule.units, "-").trim.toInt
        case Some(u) => supervision.courseUnit = u

      Params.getLong("teacher.id") match
        case None =>
          schedule.task.people foreach { p =>
            supervision.teacher = entityDao.get(classOf[Teacher], p.get("id").getOrElse("0").toString.toLong)
          }
        case Some(tid) => supervision.teacher = entityDao.get(classOf[Teacher], tid)
    }
    supervision.form = form
    supervision.lateStdCount = Params.getInt("supervision.lateStdCount").get
    val teachingOnTime = Params.getBoolean("supervision.teachingOnTime").get
    supervision.teachingOnTime = teachingOnTime
    supervision.updatedAt = Instant.now

    var score = 0
    for (field <- form.fields) {
      if (field.indicator.isEmpty) supervision.setText(field, Params.get("field_" + field.id).get)
      else {
        var s = 0
        if (field.selective) {
          val scores = Params.getAll("field_" + field.id + "_score", classOf[Int])
          scores.find(_ > 0) match
            case None => s = Params.getInt("field_" + field.id).get
            case Some(x) => s = x
        }
        else if (field.score.nonEmpty || field.selective) s = Params.getInt("field_" + field.id).get
        else s = -1
        if (s >= 0) {
          score += s
          supervision.setScore(field, s)
        }
      }
    }
    supervision.score = score
    entityDao.saveOrUpdate(supervision)
  }

  def getOrCreate(clazz: Clazz, assessor: User): Supervision = {
    val query = OqlBuilder.from(classOf[Supervision], "a")
    query.where("a.clazz=:clazz and a.assessor=:assessor", clazz, assessor)
    val assessments = entityDao.search(query)
    var sv: Supervision = null
    if (assessments.isEmpty) {
      sv = new Supervision
      sv.assessor = assessor
      sv.lessonStdCount = clazz.enrollment.stdCount
      sv.clazz = clazz
      if (clazz.teachers.size == 1) sv.teacher = clazz.teachers.head
      sv.teachingOnTime = true
      sv.lateStdCount = 0
      val roomNames = Collections.newSet[String]
      for (ca <- clazz.schedule.activities) {
        val rn = ca.rooms.map(_.name).mkString(",")
        if (Strings.isNotEmpty(rn)) roomNames.add(rn)
      }
      sv.room = roomNames.mkString(",")
      sv.updatedAt = Instant.now
    }
    else sv = assessments.head
    sv
  }

  def report(clazz: Clazz, assessor: User): Unit = {
    val supervision = getSupervision(clazz, assessor)
    if (null != supervision) {
      ActionContext.current.attribute("supervision", supervision)
    }
  }

  private def getSupervision(clazz: Clazz, assessor: User): Supervision = {
    val query = OqlBuilder.from(classOf[Supervision], "a")
    query.where("a.clazz=:clazz and a.assessor=:assessor", clazz, assessor)
    entityDao.search(query).headOption.orNull
  }
}
