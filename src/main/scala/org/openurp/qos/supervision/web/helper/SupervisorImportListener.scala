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

import org.beangle.commons.lang.Strings
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.doc.transfer.importer.{ImportListener, ImportResult}
import org.openurp.base.model.{Project, User}
import org.openurp.base.service.{SemesterService, UserCategories}
import org.openurp.qos.supervision.model.Supervisor

import java.time.LocalDate

class SupervisorImportListener(entityDao: EntityDao, project: Project,
                               semesterService: SemesterService) extends ImportListener {
  var beginOn: LocalDate = _

  override def onStart(tr: ImportResult): Unit = {
    val semester = semesterService.get(project, LocalDate.now)
    beginOn = semester.beginOn
  }

  override def onItemStart(tr: ImportResult): Unit = {
    val data = transfer.curData
    for (user <- data.get("user")) {
      var code = user.toString
      code = Strings.replace(code, " ", "")
      val sQuery = OqlBuilder.from(classOf[User], "u")
      sQuery.where("u.code = :q or u.name = :q", code)
      sQuery.where("u.category.id in(:categoryIds)", List(UserCategories.Teacher, UserCategories.Manager, UserCategories.Other))
      sQuery.cacheable()
      val users = entityDao.search(sQuery)
      if (users.size == 1) {
        val supervisors = entityDao.findBy(classOf[Supervisor], "user", users.head)
        if supervisors.size == 1 then
          transfer.current = supervisors.head
        else
          val newer = new Supervisor
          newer.user = users.head
          newer.beginOn = beginOn
          transfer.current = newer
      } else {
        tr.addFailure("不能找到唯一的用户", code)
      }
    }
  }

  override def onItemFinish(tr: ImportResult): Unit = {
    val supervisor = transfer.current.asInstanceOf[Supervisor]
    if (supervisor.user != null) {
      entityDao.saveOrUpdate(supervisor)
    }
  }
}
