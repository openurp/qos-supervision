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

import org.beangle.data.dao.OqlBuilder
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.helper.QueryHelper
import org.openurp.base.model.{Project, Semester}
import org.openurp.qos.supervision.model.*

class AdminAction extends DepartAction {
  protected override def getLevels(): Seq[SupervisingLevel] = {
    entityDao.getAll(classOf[SupervisingLevel])
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
    builder.tailOrder(alias + ".id")
    builder.limit(getPageLimit)
  }
}
