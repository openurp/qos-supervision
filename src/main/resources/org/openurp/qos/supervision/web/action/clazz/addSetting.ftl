[#ftl]
[@b.head/]
[@b.toolbar title="添加听课课程"]bar.addBack();[/@]
  [@b.form theme="list" action="!addCategory"]
    [@b.field label="学年学期"]${semester.schoolYear}学年度 ${semester.name}学期[/@]
    [@b.select name="category.id" label="课程分类" items=categories value=Parameters['category.id']!'' required="true" style="width:300px"/]
    [@b.textarea name="crns" label="课程序号" rows="10" cols="60" required="true" style="width:400px" comment="序号间使用回车，制表符（tab）或者逗号相隔"/]
    [@b.formfoot]
      [@b.submit value="添加"/]
      <input name="supervisionClazz.semester.id" type="hidden" value="${semester.id}"/>
    [/@]
  [/@]
[@b.foot/]