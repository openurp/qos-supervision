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

import org.beangle.commons.activation.MediaTypes
import org.beangle.data.dao.OqlBuilder
import org.beangle.doc.excel.schema.ExcelSchema
import org.beangle.doc.transfer.importer.ImportSetting
import org.beangle.doc.transfer.importer.listener.ForeignerListener
import org.beangle.webmvc.annotation.response
import org.beangle.webmvc.view.{Stream, View}
import org.beangle.webmvc.support.action.{ExportSupport, ImportSupport, RestfulAction}
import org.openurp.base.model.Project
import org.openurp.qos.supervision.model.{SupervisingLevel, Supervisor}
import org.openurp.qos.supervision.web.helper.SupervisorImportListener
import org.openurp.starter.web.support.ProjectSupport

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class SupervisorAction extends RestfulAction[Supervisor], ProjectSupport, ImportSupport[Supervisor], ExportSupport[Supervisor] {

  override protected def indexSetting(): Unit = {
    given project: Project = getProject

    put("levels", getCodes(classOf[SupervisingLevel]))
    put("departs", getDeparts)
    super.indexSetting()
  }

  override protected def editSetting(supervisor: Supervisor): Unit = {
    given project: Project = getProject

    if (!supervisor.persisted) {
      val semester = getSemester
      supervisor.beginOn = semester.beginOn
    }
    put("levels", getCodes(classOf[SupervisingLevel]))
    super.editSetting(supervisor)
  }

  override protected def getQueryBuilder: OqlBuilder[Supervisor] = {
    val query = super.getQueryBuilder
    queryByDepart(query, "supervisor.user.department")
    query
  }

  @response
  def downloadTemplate(): View = {
    given project: Project = getProject

    val levels = getCodes(classOf[SupervisingLevel]).map(x => x.code + " " + x.name)
    val schema = new ExcelSchema()
    val sheet = schema.createScheet("数据模板")
    sheet.title("督导模板")
    sheet.remark("特别说明：\n1、不可改变本表格的行列结构以及批注，否则将会导入失败！\n2、须按照规格说明的格式填写。\n3、可以多次导入，重复的信息会被新数据更新覆盖。\n4、保存的excel文件名称可以自定。")
    sheet.add("账户或姓名", "user").length(15).required()
    sheet.add("督导类型", "supervisor.level.code").ref(levels).required()
    sheet.add("生效日期", "supervisor.beginOn").date().remark("默认本学期开始")
    sheet.add("结束日期", "supervisor.endOn").date()
    sheet.add("备注", "supervisor.remark")
    val os = new ByteArrayOutputStream()
    schema.generate(os)
    Stream(new ByteArrayInputStream(os.toByteArray), MediaTypes.ApplicationXlsx, "督导模板.xlsx")
  }

  override protected def configImport(setting: ImportSetting): Unit = {
    val fl = new ForeignerListener(entityDao)
    fl.addForeigerKey("name")
    setting.listeners = List(fl, new SupervisorImportListener(entityDao, getProject, semesterService))
  }
}
