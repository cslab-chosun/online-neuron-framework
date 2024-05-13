package neuron.components

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import neuron.utils._

class ROO4_Blackbox(width: Int) extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val clock = Input(Clock())
    val reset = Input(Bool())
    val xp = Input(Bool())
    val xn = Input(Bool())
    val yp = Input(Bool())
    val yn = Input(Bool())
    val zp = Output(Bool())
    val zn = Output(Bool())
    val sum1 = Output(SInt((4).W))
    val sum2 = Output(SInt((4).W))
    val sum_total = Output(SInt((4).W))
  })

  setInline(
    "ROO4_Blackbox.v",
    s"""module ROO4_Blackbox (
       |    input clock, reset,
       |    input xp, xn, yp, yn,
       |    output zp, zn,
       |    output [3:0] sum1,
       |    output [3:0] sum2,
       |    output [3:0] sum_total
       |);
       |
       |// Instantiate VHDL module
       |ROO4 #(
       |    .width(${width})
       |) 
       |  ROO4_instance (
       |    .clk(clock),
       |    .rst(reset),
       |    .xp(xp),
       |    .xn(xn),
       |    .yp(yp),
       |    .yn(yn),
       |    .zp(zp),
       |    .zn(zn),
       |    .sum1(sum1),
       |    .sum2(sum2),
       |    .sum_total(sum_total)
       |);
       |
       |endmodule
    """.stripMargin
  )

}

class ROO4_Verilog(width: Int) extends Module {
  val io = IO(new Bundle {
    val xp = Input(Bool())
    val xn = Input(Bool())
    val yp = Input(Bool())
    val yn = Input(Bool())
    val zp = Output(Bool())
    val zn = Output(Bool())
    val sum1 = Output(SInt(4.W))
    val sum2 = Output(SInt(4.W))
    val sum_total = Output(SInt(4.W))
  })

  val roo4 = Module(new ROO4_Blackbox(width))

  roo4.io.clock := clock
  roo4.io.reset := reset
  roo4.io.xp := io.xp
  roo4.io.xn := io.xn
  roo4.io.yp := io.yp
  roo4.io.yn := io.yn
  io.zp := roo4.io.zp
  io.zn := roo4.io.zn
  io.sum1 := roo4.io.sum1
  io.sum2 := roo4.io.sum2
  io.sum_total := roo4.io.sum_total
}

object ROO4_Verilog {

  def apply(
      width: Int
  )(
      xp: Bool,
      xn: Bool,
      yp: Bool,
      yn: Bool
  ): (Bool, Bool) = {

    val rOO4_VerilogModule = Module(
      new ROO4_Verilog(
        width
      )
    )

    val zp = Wire(Bool())
    val zn = Wire(Bool())

    //
    // Configure the input signals
    //
    rOO4_VerilogModule.io.xp := xp
    rOO4_VerilogModule.io.xn := xn
    rOO4_VerilogModule.io.yp := yp
    rOO4_VerilogModule.io.yn := yn

    zp := rOO4_VerilogModule.io.zp
    zn := rOO4_VerilogModule.io.zn

    //
    // Return the output result
    //
    (zp, zn)
  }
}

object MainOnlineMultiplierGenerator extends App {

  //
  // Generate verilog files
  //
  println(
    ChiselStage.emitSystemVerilog(
      new ROO4_Verilog(
        4 // test
      ),
      firtoolOpts = Array(
        "-disable-all-randomization",
        "-strip-debug-info",
        "--split-verilog", // The intention for this argument (and next argument) is to separate generated files.
        "-o",
        "generated/online/multiplier/"
      )
    )
  )
}
