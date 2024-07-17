package Memorija;
import java.util.ArrayList;

    public class Block {
        private int size;
        private ArrayList<String> content = new ArrayList<>();
        private int address;
        private boolean occupied;
        private String fileName;

        public Block(int address, int size) {
            this.address = address;
            this.size = size;
            this.occupied = false;
        }

        // Getters and Setters

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public ArrayList<String> getContent() {
            return content;
        }

        public void setContent(ArrayList<String> content) {
            this.content = content;
        }

        public int getAddress() {
            return address;
        }

        public void setAddress(int address) {
            this.address = address;
        }

        public boolean isOccupied() {
            return occupied;
        }

        public void setOccupied(boolean occupied) {
            this.occupied = occupied;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }


