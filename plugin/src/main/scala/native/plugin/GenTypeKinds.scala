package native
package plugin

import scala.tools.nsc._
import native.gir

trait GenTypeKinds extends SubComponent {
  import global._, definitions._

  def genClassDefn(sym: Symbol): gir.Node

  sealed abstract class Kind
  final case class PrimitiveKind(sym: Symbol) extends Kind
  final case class BottomKind(sym: Symbol) extends Kind
  final case class ClassKind(sym: Symbol) extends Kind
  final case class ArrayKind(of: Kind) extends Kind

  val ObjectKind  = ClassKind(ObjectClass)

  val NullKind    = BottomKind(NullClass)
  val NothingKind = BottomKind(NothingClass)

  val UnitKind    = PrimitiveKind(UnitClass)
  val BooleanKind = PrimitiveKind(BooleanClass)
  val ByteKind    = PrimitiveKind(ByteClass)
  val CharKind    = PrimitiveKind(CharClass)
  val ShortKind   = PrimitiveKind(ShortClass)
  val IntKind     = PrimitiveKind(IntClass)
  val LongKind    = PrimitiveKind(LongClass)
  val FloatKind   = PrimitiveKind(FloatClass)
  val DoubleKind  = PrimitiveKind(DoubleClass)

  def genRefKind(sym: Symbol, targs: List[Type] = Nil): Kind = sym match {
    case NullClass    => NullKind
    case NothingClass => NothingKind
    case ArrayClass   => ArrayKind(genKind(targs.head))
    case _            => ClassKind(sym)
  }

  lazy val genPrimitiveKind: PartialFunction[Symbol, Kind] = {
    case UnitClass    => UnitKind
    case BooleanClass => BooleanKind
    case ByteClass    => ByteKind
    case CharClass    => CharKind
    case ShortClass   => ShortKind
    case IntClass     => IntKind
    case LongClass    => LongKind
    case FloatClass   => FloatKind
    case DoubleClass  => DoubleKind
  }

  def genPrimitiveOrRefKind(sym: Symbol, targs: List[Type] = Nil): Kind =
    genPrimitiveKind.applyOrElse(sym, genRefKind((_: Symbol), targs))

  def genKind(t: Type): Kind = t.normalize match {
    case ThisType(ArrayClass)            => ObjectKind
    case ThisType(sym)                   => genRefKind(sym)
    case SingleType(_, sym)              => genPrimitiveOrRefKind(sym)
    case ConstantType(_)                 => genKind(t.underlying)
    case TypeRef(_, sym, args)           => genPrimitiveOrRefKind(sym, args)
    case ClassInfoType(_, _, ArrayClass) => abort("ClassInfoType to ArrayClass!")
    case ClassInfoType(_, _, sym)        => genPrimitiveOrRefKind(sym)
    case t: AnnotatedType                => genKind(t.underlying)
    case tpe: ErasedValueType            => genRefKind(tpe.valueClazz)
  }

  def genType(tpe: Type): gir.Node = toIRType(genKind(tpe))

  def toIRType(kind: Kind): gir.Node = kind match {
    case PrimitiveKind(sym) =>
      sym match {
        case UnitClass    => gir.Prim.Unit
        case BooleanClass => gir.Prim.Bool
        case ByteClass    => gir.Prim.I8
        case CharClass    => gir.Prim.I16
        case ShortClass   => gir.Prim.I16
        case IntClass     => gir.Prim.I32
        case LongClass    => gir.Prim.I64
        case FloatClass   => gir.Prim.F32
        case DoubleClass  => gir.Prim.F64
      }
    case BottomKind(sym) =>
      sym match {
        case NullClass    => gir.Prim.Null
        case NothingClass => gir.Prim.Nothing
      }
    case ClassKind(sym) => genClassDefn(sym)
    case ArrayKind(of)  => gir.Defn.ArrayClass(toIRType(of))
  }
}
