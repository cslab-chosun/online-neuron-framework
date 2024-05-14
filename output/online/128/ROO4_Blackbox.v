module ROO4_Blackbox (
    input clock, reset,
    input xp, xn, yp, yn,
    output zp, zn,
    output [3:0] sum1,
    output [3:0] sum2,
    output [3:0] sum_total
);

// Instantiate VHDL module
ROO4 #(
    .width(4)
) 
  ROO4_instance (
    .clk(clock),
    .rst(reset),
    .xp(xp),
    .xn(xn),
    .yp(yp),
    .yn(yn),
    .zp(zp),
    .zn(zn),
    .sum1(sum1),
    .sum2(sum2),
    .sum_total(sum_total)
);

endmodule
    
