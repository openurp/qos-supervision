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

package org.openurp.qos.supervision.service.impl

import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.base.model.{Department, Semester, User}
import org.openurp.qos.supervision.model.{SupervisionForm, Supervisor}
import org.openurp.qos.supervision.service.SupervisionService

class SupervisionServiceImpl extends SupervisionService {

  var entityDao: EntityDao = _

  override def getSupervisors(semester: Semester, departs: Iterable[Department]): Seq[Supervisor] = {
    val query = OqlBuilder.from(classOf[Supervisor], "s")
    query.where("s.user.department in(:departs)", departs)
    query.where("s.beginOn <= :endOn and (s.endOn is null or :beginOn <= s.endOn)", semester.endOn, semester.beginOn)
    entityDao.search(query)
  }

  override def getSupervisor(semester: Semester, user: User): Option[Supervisor] = {
    val query = OqlBuilder.from(classOf[Supervisor], "s")
    query.where("s.user=:user", user)
    query.where("s.beginOn <= :endOn and (s.endOn is null or :beginOn <= s.endOn)", semester.endOn, semester.beginOn)
    entityDao.search(query).headOption
  }

  override def getSupervisionForm(semester: Semester): SupervisionForm = {
    val query = OqlBuilder.from(classOf[SupervisionForm], "f")
    query.where("f.beginOn <= :date and (f.endOn is null or f.endOn >= :date)", semester.beginOn)
    entityDao.search(query).head
  }
}
