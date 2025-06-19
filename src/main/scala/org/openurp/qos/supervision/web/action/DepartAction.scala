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
import org.beangle.doc.transfer.exporter.ExcelWriter
import org.beangle.webmvc.view.View
import org.beangle.web.servlet.util.RequestUtils
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.{Project, Semester, User}
import org.openurp.edu.clazz.model.Clazz
import org.openurp.edu.schedule.service.LessonSchedule
import org.openurp.qos.supervision.model.*
import org.openurp.qos.supervision.service.SupervisionService
import org.openurp.qos.supervision.web.helper.SupervisionHelper
import org.openurp.starter.web.support.ProjectSupport

import java.time.LocalTime
import java.util

class DepartAction extends RestfulAction[Supervision], ProjectSupport {

  var supervisionService: SupervisionService = _

  protected def getLevels(): Seq[SupervisingLevel] = {
    val query = OqlBuilder.from(classOf[SupervisingLevel], "sl")
    query.where("sl.schoolOnly=true")
    query.cacheable()
    entityDao.search(query)
  }

  override protected def indexSetting(): Unit = {
    super.indexSetting()

    given project: Project = getProject

    put("project", project)
    put("levels", getLevels())
    put("departs", getDeparts)
    put("semester", getSemester)
  }

  override protected def getQueryBuilder: OqlBuilder[Supervision] = {
    given project: Project = getProject

    val query = super.getQueryBuilder
    get("teacherName") foreach { teacherName =>
      query.where("exists(from supervision.clazz.teachers as t where t.name like :teacherName)", "%" + teacherName + "%")
    }
    query.where("supervision.level in(:levels)", getLevels())
    query.where("supervision.assessor.department in(:departs)", getDeparts)
  }

  def clazzes(): View = {
    given project: Project = getProject

    val semester = entityDao.get(classOf[Semester], getInt("clazz.semester.id", 0))
    val query = OqlBuilder.from(classOf[Clazz], "clazz")
    query.where("clazz.semester=:semester", semester)
    query.where("clazz.project=:project", project)
    query.where("clazz.teachDepart in(:departs)", getDeparts)
    get("q") foreach { q =>
      if (Strings.isNotBlank(q)) {
        query.where("clazz.crn like :q or clazz.course.code like :q or clazz.course.name like :q or exists(from clazz.teachers t where t.name like :q)", "%" + q + "%")
      }
    }
    query.limit(1, 20)
    val clazzes = entityDao.search(query)
    put("clazzes", clazzes)
    forward()
  }

  def inputSearch(): View = {
    given project: Project = getProject

    val semester = entityDao.get(classOf[Semester], getInt("supervision.clazz.semester.id", 0))
    put("semester", semester)
    val supervisors = supervisionService.getSupervisors(semester, getDeparts)
    put("supervisors", supervisors)
    forward()
  }

  /** 录入听课内容
   *
   * @return
   */
  def input(): View = {
    val supervisor = entityDao.get(classOf[Supervisor], getLongId("supervisor"))
    put("supervisor", supervisor)
    val clazz = entityDao.get(classOf[Clazz], getLongId("clazz"))
    put("clazz", clazz)
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
    val supervision = new SupervisionHelper(entityDao).getOrCreate(clazz, supervisor.user)
    put("supervision", supervision)
    put("assessor", supervision.assessor)
    forward("form")
  }

  /** 提交听课内容
   *
   * @return
   */
  def submit(): View = {
    val clazz = entityDao.get(classOf[Clazz], getLongId("clazz"))
    val assessor = entityDao.get(classOf[User], getLongId("assessor"))
    val supervision = new SupervisionHelper(entityDao).getOrCreate(clazz, assessor)
    val form = entityDao.get(classOf[SupervisionForm], getLongId("supervisionForm"))
    val supervisor = supervisionService.getSupervisor(clazz.semester, assessor)
    supervision.level = supervisor.get.level
    new SupervisionHelper(entityDao).save(form, supervision)
    redirect("info", "&id=" + supervision.id, "info.save.success")
  }

  def exportData(): View = {
    val semester = entityDao.get(classOf[Semester], getInt("supervision.clazz.semester.id", 0))
    response.setContentType("application/vnd.ms-excel;charset=GBK")
    RequestUtils.setContentDisposition(response, semester.schoolYear + "学年" + semester.name + "学期 领导听课结果.xlsx")
    val form = supervisionService.getSupervisionForm(semester)
    val query = getQueryBuilder
    query.limit(null)
    val supervisions = entityDao.search(query)
    val writer = new ExcelWriter(response.getOutputStream)
    writer.createScheet("分数明细")
    var titles = List("学年度", "学期", "听课人", "听课人所在部门", "听课日期", "开课院系", "课程序号", "课程代码", "课程名称", "授课教师").toBuffer
    val scoreFields = Collections.newBuffer[SupervisionField]
    for (f <- form.fields) {
      if (f.maxlength == 0) {
        val hasScore = supervisions.exists(s => s.getScore(f) > 0)
        if (hasScore) scoreFields.addOne(f)
      }
    }
    for (f <- scoreFields) {
      titles.addOne(f.name)
    }
    writer.writeHeader(Some(semester.schoolYear + "学年" + semester.name + "学期 领导听课分数"), titles.toArray)
    if (supervisions.nonEmpty) {
      for (s <- supervisions) {
        val data = new util.ArrayList[Any]
        data.add(s.clazz.semester.schoolYear)
        data.add(s.clazz.semester.name)
        data.add(s.assessor.name)
        data.add(s.assessor.department.name)
        data.add(s.assessOn)
        data.add(s.clazz.teachDepart.name)
        data.add(s.clazz.crn)
        data.add(s.clazz.course.code)
        data.add(s.clazz.course.name)
        data.add(if null == s.teacher then "" else s.teacher.name)
        for (f <- scoreFields) {
          val score = s.getScore(f)
          if (score < 0) data.add("")
          else data.add(score)
        }
        writer.write(data.toArray)
      }
    }
    writer.createScheet("意见汇总")
    titles = List("学年度", "学期", "听课人", "听课人所在部门", "听课日期", "开课院系", "课程序号", "课程代码", "课程名称", "授课教师").toBuffer
    for (f <- form.fields) {
      if (f.maxlength > 0) titles.addOne(f.name)
    }
    writer.writeHeader(Some(semester.schoolYear + "学年" + semester.name + "学期 领导听课意见汇总"), titles.toArray)
    if (supervisions.nonEmpty) {
      for (s <- supervisions) {
        val data = new util.ArrayList[Any]
        data.add(s.clazz.semester.schoolYear)
        data.add(s.clazz.semester.name)
        data.add(s.assessor.name)
        data.add(s.assessor.department.name)
        data.add(s.assessOn)
        data.add(s.clazz.teachDepart.name)
        data.add(s.clazz.crn)
        data.add(s.clazz.course.code)
        data.add(s.clazz.course.name)
        data.add(if null == s.teacher then "" else s.teacher.name)
        for (f <- form.fields) {
          if (f.maxlength > 0) data.add(s.getText(f))
        }
        writer.write(data.toArray)
      }
    }
    writer.close()
    null
  }

}
