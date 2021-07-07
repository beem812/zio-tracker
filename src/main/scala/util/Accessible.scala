package util

import zio._

trait Accessible[R] {
  def apply[E, A](f: R => ZIO[Any, E, A])(implicit tag: Tag[R]): ZIO[Has[R], E, A] = ZIO.serviceWith[R](f)

}
