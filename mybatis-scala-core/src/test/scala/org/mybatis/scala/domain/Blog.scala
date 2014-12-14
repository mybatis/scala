package org.mybatis.scala.domain

case class Blog(var title: String) {
  var id: Int = _
  var entries: Seq[Entry] = Seq.empty
}

object Blog {
  def apply(id: Int, title: String): Blog = {
    val blog = Blog(title)
    blog.id = id
    blog
  }
}

case class Entry(var body: String) {
  var id: Int = _
}