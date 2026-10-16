; Nuvoton MS51FB9AE educational demo
; Instructions used:
; MOV A,#data
; MOV Rn,#data
; ADD A,Rn
; INC A
; ANL A,#data
; JZ relative_offset
; SUBB A,Rn
; RET (used as top-level termination in this simulator)

MOV A, #05H
MOV R0, #03H
ADD A, R0
INC A
ANL A, #0FH
MOV R1, #02H
SUBB A, R1
RET
