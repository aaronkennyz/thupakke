; Nuvoton MS51FB9AE / 8051-family style validation program
; Purpose: validate FIFO ordering using internal RAM locations.
; Queue storage: 30H, 31H, 32H
; Expected dequeue order: 10H, 20H, 30H
;
; The Java simulator's ENQ/DEQ demonstration mirrors this ordering.
; The processor-specific assembly below uses standard 8051-style
; register and indirect-memory operations for validation documentation.

ORG 0000H

MOV R0, #30H
MOV A, #10H
MOV @R0, A
INC R0
MOV A, #20H
MOV @R0, A
INC R0
MOV A, #30H
MOV @R0, A

MOV R0, #30H
MOV A, @R0
MOV 40H, A       ; Expected first dequeue = 10H
INC R0
MOV A, @R0
MOV 41H, A       ; Expected second dequeue = 20H
INC R0
MOV A, @R0
MOV 42H, A       ; Expected third dequeue = 30H

; Expected:
; 40H = 10H
; 41H = 20H
; 42H = 30H

END
