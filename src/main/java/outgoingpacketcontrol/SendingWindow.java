package outgoingpacketcontrol;

import general.Protocol;
import host.Host;
import incomingpacketcontrol.IncomingPacket;

import java.util.HashMap;

// TODO: Clean up
public class SendingWindow {

    private Host host;
    private HashMap<Integer, SendingTask> taskHashMap;
    private Integer currentTaskNo;

    public SendingWindow(Host host) {
        this.host = host;
        taskHashMap = new HashMap<>();
        currentTaskNo = 0;
    }

    // When a host begins transmitting a new task, this is made to control the acks that will come back
    public void addTask(OutgoingData outgoingData) {
        int taskNo = getNewTask();
        outgoingData.setTaskNo(taskNo);
        SendingTask newTask = new SendingTask(host, outgoingData);
        taskHashMap.put(taskNo, newTask);
        (new Thread(newTask)).start();
    }

    public void processReceivedAck(IncomingPacket ack) {
        int taskNo = ack.getTaskNo();
        int sequenceNo = ack.getSequenceNo();

        if (taskExists(taskNo)) {
            int totalSequenceNo = getTotalSequenceNo(taskNo, sequenceNo);

            if (Protocol.showInfo) {
                System.out.println("Received: " + ack.getCommand() + "-" + ack.getSequenceCmd() + "-" + ack.getTaskNo() + "-" + ack.getSequenceNo() + "-" + totalSequenceNo);
            }

            if (getTask(taskNo).getTotalLAF() < getLAFTotalSequenceNo(taskNo, ack.getLAF())) {
                getTask(taskNo).setLAF(ack.getLAF());
                getTask(taskNo).setTotalLAF(getLAFTotalSequenceNo(taskNo, ack.getLAF()));
            }

            if (totalSequenceNo != -1) {
                String receivedAck = ackString(taskNo, totalSequenceNo);
                getTask(taskNo).addReceivedAck(receivedAck);
                update(taskNo);
            } else {
                if (Protocol.showInfo) {
                    System.out.println("processReceivedAck(ack): ack with sequenceNo " + sequenceNo + " was not expected");
                }
            }
        } else {
            if (Protocol.showInfo) {
                System.out.println("processReceivedAck(ack): taskNo not known, ack was not expected");
            }
        }
    }

    public void processPauseResume(IncomingPacket packet) {
        int taskNo = packet.getTaskNo();
        if (packet.getCommand().equals(Protocol.PAUSE)) {
            getTask(taskNo).pause();
        } else {
            getTask(taskNo).resume();
        }
    }

    private boolean taskExists(int taskNo) {
        return taskHashMap.containsKey(taskNo);
    }

    private SendingTask getTask(int taskNo) {
        return taskHashMap.get(taskNo);
    }

    public void confirmPacketSend(int taskNo, int sequenceNo, int totalSequenceNo) {
        if (taskExists(taskNo)) {
            getTask(taskNo).setLFS(sequenceNo);
            getTask(taskNo).setTotalLFS(totalSequenceNo);
        } else {
            if (Protocol.showInfo) {
                System.out.println("confirmPacketSend(taskNo, sequenceNo, totalSequenceNo): taskNo " + taskNo + " not known");
            }
        }
    }

    public boolean canSend(int taskNo, int totalSequenceNo) {
        return totalSequenceNo > getTask(taskNo).getTotalLAR() && totalSequenceNo <= (getTask(taskNo).getTotalLAR() + Protocol.WS) && totalSequenceNo <= getTask(taskNo).getTotalLAF();
    }

    public boolean hasAck(int taskNo, int totalSequenceNo) {
        String requestedAck = ackString(taskNo, totalSequenceNo);
        if (taskExists(taskNo)) {
            return getTask(taskNo).hasAck(requestedAck);
        } else {
            return false;
        }
    }

    private String ackString(int taskNo, int totalSequenceNo) {
        return taskNo + Protocol.DELIMITER + totalSequenceNo;
    }

    private int getSequenceNo(int totalSequenceNo) {
        return totalSequenceNo % Protocol.maxSequenceNo;
    }

    private int getTotalSequenceNo(int taskNo, int sequenceNo) {
        int LAR = getTask(taskNo).getLAR();
        int LFS = getTask(taskNo).getLFS();
        int cycleNo = getTask(taskNo).getCycleNo();

        if (LAR < LFS) {
            if (sequenceNo > LAR && sequenceNo <= LFS) {
                return (cycleNo * Protocol.maxSequenceNo) + sequenceNo;
            } else {
                // Do nothing, ack is not expected
                return -1;
            }
        } else if (LAR > LFS) {
            if (sequenceNo > LAR && sequenceNo < Protocol.maxSequenceNo) {
                return (cycleNo * Protocol.maxSequenceNo) + sequenceNo;
            } else if (sequenceNo >= 0 && sequenceNo <= LFS) {
                return ((cycleNo + 1) * Protocol.maxSequenceNo) + sequenceNo;
            } else if (sequenceNo > LFS && sequenceNo <= LAR) {
                // do nothing, ack is not expected
                return -1;
            } else {
                // which situations are these?
                return -1;
            }
        } else {
            // ackLAR == ackLFS?????
            return -1;
        }
    }

    private int getLAFTotalSequenceNo(int taskNo, int sequenceNo) {
        int LAR = getTask(taskNo).getLAR();
        int LFS = getTask(taskNo).getLFS();
        int minLAF = getSequenceNo(LAR + Protocol.WS - 1);
        int maxLAF = getSequenceNo(LFS + 2 * Protocol.WS);
        int cycleNo = getTask(taskNo).getCycleNo();

        if (minLAF < maxLAF) {
            if (LAR > minLAF) {
                cycleNo++;
            }

            if (sequenceNo > minLAF && sequenceNo <= maxLAF) {
                return (cycleNo * Protocol.maxSequenceNo) + sequenceNo;
            } else {
                return -1;
            }
        } else if (minLAF > maxLAF) {
            if (sequenceNo > minLAF && sequenceNo < Protocol.maxSequenceNo) {
                return (cycleNo * Protocol.maxSequenceNo) + sequenceNo;
            } else if (sequenceNo >= 0 && sequenceNo <= maxLAF) {
                return ((cycleNo + 1) * Protocol.maxSequenceNo) + sequenceNo;
            } else if (sequenceNo > maxLAF && sequenceNo <= minLAF) {
                // do nothing, ack is not expected
                return -1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private void update(int taskNo) {
        int nextAck = getTask(taskNo).getTotalLAR() + 1;

        while (getTask(taskNo).hasAck(ackString(taskNo, nextAck))) {
            getTask(taskNo).setTotalLAR(nextAck);
            getTask(taskNo).setLAR(getSequenceNo(nextAck));

            nextAck = getTask(taskNo).getTotalLAR() + 1;

            if (getTask(taskNo).getLAR() == 0 && getTask(taskNo).getNAcksReceived() > Protocol.maxSequenceNo) {
                getTask(taskNo).setCycleNo(getTask(taskNo).getCycleNo() + 1);
            }
        }
    }

    public int getNewTask() {
        while (true) {
            if (taskExists(currentTaskNo)) {
                currentTaskNo = (currentTaskNo + 1) % Protocol.maxTaskNo;
            } else {

                return currentTaskNo;
            }
        }
    }
}
