[@b.head/]
[@b.toolbar title="领导听课(${supervisor.level.name})"/]
[@base.semester_bar value=semester/]
<div class="search-panel" style="width:100%">
[@b.form action="!search" target="lessonFrame" name="actionForm" theme="html"]
  <input type="hidden" name="clazz.semester.id" value="${semester.id}"/>
    <ul style="margin: 0px;">
     [#if !supervisor.level.name?contains("学院")]
     <li>课程分类：
            <input type="radio" name="category.id" id="kind_0" checked="checked" value="">
            <label for="kind_0"  class="form-check-label">全部</label>
     [#list categories as category]
       <input type="radio" name="category.id" id="category_${category.id}" value="${category.id}">
       <label class="form-check-label" for="category_${category.id}">${category.name}</label>
     [/#list]
     </li>
     [/#if]

     <li>周&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;几：
        <input type="radio" name="clazzActivity.time.week" id="weekday_0" checked="checked" value=""><label  class="form-check-label" for="weekday_0">全部</label>
        [#assign weekdayNames=["0","星期一","星期二","星期三","星期四","星期五","星期六","星期日"] /]
        [#list weekdays as w]
        <input type="radio" name="clazzActivity.time.week" id="weekday_${w.id}" value="${w.id}"><label  class="form-check-label" for="weekday_${w.id}">${weekdayNames[w.id]}&nbsp;</label>
        [/#list]
     </li>
     <li>小&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;节：
        <input type="radio" name="clazzActivity.beginUnit" id="startUnit_0" checked="checked" value=""><label class="form-check-label" for="startUnit_0">全部</label>
        [#list 1..14 as u]
        <input type="radio" name="clazzActivity.beginUnit" id="startUnit_${u}" value="${u}"><label  class="form-check-label" for="startUnit_${u}">${u}</label>
        [/#list]
     </li>
     <li>教师姓名：<input name="teacher.name" type="text" style="width:100px"/>
     课程名称：<input name="course.name" type="text" style="width:200px" placeholder="课程名称模糊查询"/>
     </li>
     <li>听课情况：
        <input type="radio" name="supervising" id="supervising_all" checked="checked" value="all"><label  class="form-check-label" for="supervising_all">全部</label>
        <input type="radio" name="supervising" id="supervising_me" value="me"><label  class="form-check-label" for="supervising_me">我听过</label>
        <input type="radio" name="supervising" id="supervising_other" value="other"><label  class="form-check-label" for="supervising_other">有人听过</label>
        <input type="radio" name="supervising" id="supervising_none" value="none"><label  class="form-check-label" for="supervising_none">没人听过</label>
        [@b.submit class="btn btn-outline-primary btn-sm" style="margin-left: 120px;" value='查询'][/@]
     </li>
    </ul>
[/@]
</div>
[@b.div id="lessonFrame"/]
<script>
  jQuery(document).ready(function(){
    bg.form.submit("actionForm");
  });
</script>
[@b.foot/]
