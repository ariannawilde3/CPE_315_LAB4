public class ProcessInstructionHelper {
    public ProcessInstructionHelper() {

    }
    
    static public boolean detectDataHazard(Instruction idStage, Instruction ifStage) {
        boolean hazard = false;
        if (ifStage == null || idStage == null) return false;

        if(idStage.getOperationString().equals("lw") && ifStage.getOperationString().equals("lw") )
        {
            return false;
        }
        
        String ifSource = ifStage.getSource();
        String ifTarget = ifStage.getTarget();        
        //String ifDest = ifStage.getDest();
        if ((idStage.getTarget() != null && idStage.getTarget().equals(ifSource)) || 
            (idStage.getTarget() != null && idStage.getTarget().equals(ifTarget))) {
            // ||
            // (idStage.getSource() != null && idStage.getSource().equals(ifSource)) || 
            // (idStage.getSource() != null && idStage.getSource().equals(ifTarget))) {
            hazard = true;
        }
        return hazard;
    }
   
    // return true if pc is moved
    public static int ProcessInstruction(Instruction instruction) {                  
            int sourceIndex = 0;
            int targetIndex = 0;
            int destIndex = 0;
            int immediate = 0;
            int memoryAddress = 0;
            int movePcTo = -1;

            lab4.totalInstructions++;
            switch(instruction.getOperationString()){

                case "and":
                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
            
                    lab4.Registers[destIndex] = lab4.Registers[sourceIndex] & lab4.Registers[targetIndex];

                    break;

                case "or":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
        
                    lab4.Registers[destIndex] = lab4.Registers[sourceIndex] | lab4.Registers[targetIndex];

                    break;

                case "add":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
                    
                    lab4.Registers[destIndex] = lab4.Registers[sourceIndex] + lab4.Registers[targetIndex];

                    break;

                case "addi":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    lab4.Registers[targetIndex] = lab4.Registers[sourceIndex] + immediate;

                    break;

                case "sll":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
            
                    lab4.Registers[destIndex] = lab4.Registers[sourceIndex] << immediate;

                    break;

                case "sub":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
                
                    lab4.Registers[destIndex] = lab4.Registers[sourceIndex] - lab4.Registers[targetIndex];

                    break;

                case "slt":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);

                    if (lab4.Registers[sourceIndex] < lab4.Registers[targetIndex]) {
                        lab4.Registers[destIndex] = 1;
                    } else {
                        lab4.Registers[destIndex] = 0;
                    }

                    break;

                case "beq":
                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);            
            
                    if (lab4.Registers[sourceIndex] == lab4.Registers[targetIndex]) {
                        movePcTo =  getLabelAddr(instruction.getLabelName());                        
                    }

                    break;

                case "bne":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
            
                    // if(R[rs]!=R[rt])
                    // PC=PC+1+BranchAddr
                    if (lab4.Registers[sourceIndex] != lab4.Registers[targetIndex]) {
                        movePcTo = getLabelAddr(instruction.getLabelName());
                    }

                    break; 
                case "lw":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    memoryAddress = lab4.Registers[sourceIndex] + immediate;
                    lab4.Registers[targetIndex] = lab4.dataMemory[memoryAddress];

                    break; 
                case "sw":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    int memoryAddressSW = lab4.Registers[sourceIndex] + immediate;
                    lab4.dataMemory[memoryAddressSW] = lab4.Registers[targetIndex];

                    break; 

                case "j":
                    movePcTo = getLabelAddr(instruction.getLabelName());                    

                    break;

                case "jr":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    movePcTo = lab4.Registers[sourceIndex];

                    break;

                case "jal":
                
                    lab4.Registers[31] = lab4.pc + 1;
                    movePcTo = getLabelAddr(instruction.getLabelName());
                

                    break;

                default:
                    System.out.println("invalid instruction: " + instruction.getOperationString() );
                    System.exit(0);
                    break;

            }

            return movePcTo;
        }

        public static int getLabelAddr(String labelName) {
            int addr = -1;
            
            if(labelName != null && labelName != "") {
                addr = lab4.labelToLineMap.get(labelName);
            }

            return addr;
        }
    }
