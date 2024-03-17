 <table width="100%">
      <tr>
        <td  class="infoTitle" align="left" valign="bottom">
         <img src="${static_base}/eams/2.6.2/images/action/info.gif" align="top"/>
            <B><@msg.message key="action.advancedQuery.like"/></B>
        </td>
      </tr>
      <tr>
        <td  colspan="8" style="font-size:0px">
            <img src="${static_base}/eams/2.6.2/images/action/keyline.gif" height="2" width="100%" align="top">
        </td>
     </tr>
  </table>
  <table width='100%' class="searchTable" onkeypress="dwr.util.onReturn(event, search)">
      <tr>
       <td  class="infoTitle" width="35%">课程序号:</td>
       <td>
        <input type="text" name="assessment.clazz.seqNo" maxlength="32" size="10"/>
       </td>
    </tr>
      <tr>
       <td  class="infoTitle" width="35%"><@bean.message key="attr.courseName" />:</td>
       <td>
        <input type="text" name="assessment.clazz.course.name" maxlength="32" size="10" value="${(RequestParameters['task.course.name']?html)!}"/>
       </td>
    </tr>
    <tr>
       <td  class="infoTitle" width="35%"><@bean.message key="info.studentClassManager.className" />:</td>
       <td>
        <input type="text" name="assessment.clazz.teachClass.name" maxlength="32" size="10" value="${(RequestParameters['task.teachClass.name']?html)!}"/>
       </td>
    </tr>
    <tr>
       <td class="infoTitle">听课院系:</td>
       <td>
         <select name="assessment.clazz.arrangeInfo.teachDepart.id" value="" style="width:100px">
           <option value=""><@bean.message key="common.all"/></option>
           <#list (departmentList)?sort_by("code") as depart>
                <option value=${depart.id}><@i18nName depart/></option>
           </#list>
         </select>
       </td>
     </tr>
    <tr>
       <td class="infoTitle">听课类别:</td>
       <td>
         <select name="assessment.assessedBySupervisor" value="" style="width:100px">
           <option value=""><@bean.message key="common.all"/></option>
          <option value="1">职能部门领导听课</option>
          <option value="0">学院领导听课</option>
         </select>
       </td>
     </tr>
     <tr>
       <td  class="infoTitle" width="35%">听课人:</td>
       <td>
        <input type="text" name="assessment.assessor.name" maxlength="32" size="10" value="${(RequestParameters['task.teachClass.name']?html)!}"/>
       </td>
    </tr>
      <tr align="center">
       <td colspan="2">
         <button style="width:60px" class="buttonStyle" onClick="search()"><@bean.message key="action.query"/></button>
       </td>
      </tr>
  </table>
