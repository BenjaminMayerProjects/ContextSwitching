import java.util.ArrayList;
import java.util.Random;

public class Main {
    private static ProcessControlBlock currentProcess;
    private static SimProcessor processor = new SimProcessor();
    static ArrayList<ProcessControlBlock> readyList = new ArrayList<ProcessControlBlock>();
    private static int quantCount = 0;
    private static int counter;
    private static ArrayList<ProcessControlBlock> blockedList = new ArrayList<ProcessControlBlock>();


    public static void main(String[] args) {

        final int QUANTUM = 5;

        Random random = new Random();

        SimProcess process1 = new SimProcess(1532, "Chrome", random.nextInt(300) + 100);
        SimProcess process2 = new SimProcess(5435, "Firefox", random.nextInt(300) + 100);
        SimProcess process3 = new SimProcess(5436, "Internet Explorer", random.nextInt(300) + 100);
        SimProcess process4 = new SimProcess(2643, "Google Docs", random.nextInt(300) + 100);
        SimProcess process5 = new SimProcess(4764, "Google Sheets", random.nextInt(300) + 100);
        SimProcess process6 = new SimProcess(2534, "Google Drive", random.nextInt(300) + 100);
        SimProcess process7 = new SimProcess(3545, "Microsoft Word", random.nextInt(300) + 100);
        SimProcess process8 = new SimProcess(2534, "Microsoft Powerpoint", random.nextInt(300) + 100);
        SimProcess process9 = new SimProcess(3454, "Microsoft Excel", random.nextInt(300) + 100);
        SimProcess process10 = new SimProcess(5463, "Windows Update", random.nextInt(300) + 100);

        readyList.add(new ProcessControlBlock(process1));
        readyList.add(new ProcessControlBlock(process2));
        readyList.add(new ProcessControlBlock(process3));
        readyList.add(new ProcessControlBlock(process4));
        readyList.add(new ProcessControlBlock(process5));
        readyList.add(new ProcessControlBlock(process6));
        readyList.add(new ProcessControlBlock(process7));
        readyList.add(new ProcessControlBlock(process8));
        readyList.add(new ProcessControlBlock(process9));
        readyList.add(new ProcessControlBlock(process10));
        currentProcess = readyList.remove(0);
        processor.setCurrentProcess(currentProcess.getProcess());
        for (counter = 0; counter < 3000; counter++) {
            System.out.println("Step: " + counter);


            if (currentProcess != null) {
                ProcessState newState = processor.executeNextInstruction();
                if (newState.equals(ProcessState.FINISHED)) {
                    System.out.println("***Process Completed***");
                    contextSwitch(counter);
                }
                if (newState.equals(ProcessState.BLOCKED)) {
                    System.out.println("***Process Blocked***");
                    blockedList.add(currentProcess);
                    quantCount = 0;
                    contextSwitch(counter);
                }
                if (newState.equals(ProcessState.READY)) {
                    quantCount++;
                    if (quantCount >= QUANTUM) {
                        System.out.println("***QUANTUM REACHED");
                        readyList.add(currentProcess);
                        quantCount = 0;
                        contextSwitch(counter);
                    }
                }            }
            if(currentProcess == null)
            {
                if(readyList.size() >= 1) {
                    contextSwitch(counter);
                }

            }
            for (int a = 0; a < blockedList.size(); a++)
            {

                int willWeWake = random.nextInt(100);
                if(willWeWake <= 30)
                    {
                        ProcessControlBlock placeholder = blockedList.remove(a);
                        readyList.add(placeholder);
                    }
                }

        }

    }



    public static void saveProcessInfo() {
        int currInstruction = processor.getCurrInstruction();
        int reg1 = processor.getRegister1();
        int reg2 = processor.getRegister2();
        int reg3 = processor.getRegister3();
        int reg4 = processor.getRegister4();

        System.out.println("Context switch: Saving process: " + currentProcess.getProcess().getProcName() + ", Instruction" + ": " +
                currInstruction + ", R1:" + reg1 + ", R2:" + reg2 +" , R3:" + reg3 + ", R4:" + reg4);

        currentProcess.setCurrentInstruction(currInstruction);
        currentProcess.setRegister1(reg1);
        currentProcess.setRegister2(reg2);
        currentProcess.setRegister3(reg3);
        currentProcess.setRegister4(reg4);


    }
    public static void restoreProcessInfo() {

        int currInstruction = currentProcess.getCurrentInstruction();
        int reg1 = currentProcess.getRegister1();
        int reg2 = currentProcess.getRegister2();
        int reg3 = currentProcess.getRegister3();
        int reg4 = currentProcess.getRegister4();

        System.out.println("Context switch: Loading process: " + currentProcess.getProcess().getProcName() + ", Instruction" + ": " +
                currInstruction + ", R1:" + reg1 + ", R2:" + reg2 +" , R3:" + reg3 + ", R1:" + reg4);


        processor.setCurrInstruction(currInstruction);
        processor.setRegister1(reg1);
        processor.setRegister2(reg2);
        processor.setRegister3(reg3);
        processor.setRegister4(reg4);



    }

    public static void contextSwitch(int step) {
        System.out.println("Step: " + step  + "\nPerforming context switch...");
        if(currentProcess != null) {
            saveProcessInfo();
        }
        if(!readyList.isEmpty())
        {
            currentProcess = readyList.remove(0);
            restoreProcessInfo();
            processor.setCurrentProcess(currentProcess.getProcess());

        }
        else
        {
            currentProcess = null;
        }



    }
}



enum ProcessState {
    READY,
    BLOCKED,
    SUSPENDED_READY,
    SUSPENDED_BLOCKED,
    FINISHED;
}
class SimProcess
{
    private int pid;
    private String procName;
    private int totalInstructions;

    public SimProcess(int pid, String procName, int totalInstructions)
    {
        this.pid = pid;
        this.procName = procName;
        this.totalInstructions = totalInstructions;
    }

    public int getTotalInstructions() {
        return totalInstructions;
    }
    public String getStringTotalInstructions() {
        return Integer.toString(totalInstructions);
    }

    public int getPid() {
        return pid;
    }
    public String getStringPid() {
        return Integer.toString(pid);
    }

    public String getProcName() {
        return procName;
    }

    public ProcessState execute(int i)
    {
        System.out.println("Pid: " + getStringPid() + ", Process name: " + getProcName() + ", Current Instructions: " + i);
        if(i >= totalInstructions)
        {
            return ProcessState.FINISHED;
        }
        else
        {
            Random random = new Random();
            int randomNumber = random.nextInt(100);

            if (randomNumber <= 15) {
                return ProcessState.BLOCKED;
            }
            else {
                return ProcessState.READY;
            }
        }
    }
}
class SimProcessor
{
    private int register1;
    private int register2;
    private int register3;
    private int register4;
    private int currInstruction;
    private SimProcess currentProcess;

    public int getCurrInstruction() {
        return currInstruction;
    }

    public void setCurrInstruction(int currInstruction) {
        this.currInstruction = currInstruction;
    }

    public int getRegister1() {
        return register1;
    }

    public void setRegister1(int register1) {
        this.register1 = register1;
    }

    public int getRegister2() {
        return register2;
    }

    public void setRegister2(int register2) {
        this.register2 = register2;
    }

    public int getRegister3() {
        return register3;
    }

    public void setRegister3(int register3) {
        this.register3 = register3;
    }

    public int getRegister4() {
        return register4;
    }

    public void setRegister4(int register4) {
        this.register4 = register4;
    }


    public SimProcess getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(SimProcess currentProcess) {
        this.currentProcess = currentProcess;
    }
    public ProcessState executeNextInstruction()
    {
        ProcessState newState = currentProcess.execute(currInstruction);
        currInstruction++;
        Random random = new Random();
        setRegister1(random.nextInt(1000));
        setRegister2(random.nextInt(1000));
        setRegister3(random.nextInt(1000));
        setRegister4(random.nextInt(1000));



        return newState;
    }

}
class ProcessControlBlock
{
    private SimProcess process;
    private int register1;
    private int register2;
    private int register3;
    private int register4;
    private int currentInstruction;
    public ProcessControlBlock(SimProcess process)
    {
        this.process = process;
    }

    public SimProcess getProcess() {
        return process;
    }

    public void setCurrentInstruction(int currentInstruction) {
        this.currentInstruction = currentInstruction;
    }

    public int getCurrentInstruction() {
        return currentInstruction;
    }
    public int getRegister1() {
        return register1;
    }

    public void setRegister1(int register1) {
        this.register1 = register1;
    }

    public int getRegister2() {
        return register2;
    }

    public void setRegister2(int register2) {
        this.register2 = register2;
    }

    public int getRegister3() {
        return register3;
    }

    public void setRegister3(int register3) {
        this.register3 = register3;
    }

    public int getRegister4() {
        return register4;
    }

    public void setRegister4(int register4) {
        this.register4 = register4;
    }


}
