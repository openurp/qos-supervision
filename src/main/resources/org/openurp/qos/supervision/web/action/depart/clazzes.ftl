[#ftl attributes={"content_type":"application/json"}]
[[#list clazzes as clz]{"id":"${clz.id}","name":"${clz.crn} ${clz.course.code} ${clz.course.name} [#list clz.teachers as t]${t.name}[#sep] [/#list]"}[#sep],[/#list]]
