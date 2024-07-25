    package FileSistem;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    public class FileSystem {
        private Directory root;
        private Directory currentDir;
        private List<Integer> freeBlocks; // Lista slobodnih blokova

        public FileSystem() {
            this.root = new Directory(System.getProperty("user.dir"), null);
            this.currentDir = root;
            this.freeBlocks = new ArrayList<>();
            initializeRoot();
            initializeFreeBlocks();
        }

        private void initializeRoot() {
            String currentWorkingDir = System.getProperty("user.dir");
            System.out.println("Current working directory: " + currentWorkingDir);
            java.io.File file = new java.io.File(currentWorkingDir);
            initializeDirectory(file, root);
        }

        private void initializeDirectory(java.io.File file, Directory directory) {
            java.io.File[] filesList = file.listFiles();
            if (filesList != null) {
                for (java.io.File f : filesList) {
                    if (f.isDirectory()) {
                        Directory subDirectory = new Directory(f.getAbsolutePath(), directory);
                        directory.addSubDirectory(subDirectory);
                        initializeDirectory(f, subDirectory);
                    } else {
                        directory.addFile(new File(f.getName(), (int) f.length()));
                    }
                }
            }
        }

        private void initializeFreeBlocks() {
            // Inicijalizujemo slobodne blokove
            for (int i = 0; i < 100; i++) { // Pretpostavimo da imamo 100 blokova
                freeBlocks.add(i);
            }
        }

        private int allocateBlock() {
            if (freeBlocks.isEmpty()) {
                System.out.println("Nema dostupnih blokova za alokaciju.");
                return -1;
            }
            Random rand = new Random();
            return freeBlocks.remove(rand.nextInt(freeBlocks.size()));
        }

        public List<Integer> allocateBlocks(int size) {
            if (!hasSufficientFreeBlocks(size)) {
                System.out.println("Nema dovoljno slobodnih blokova za alokaciju.");
                return new ArrayList<>(); // Vraćanje prazne liste
            }

            List<Integer> allocatedBlocks = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int blockIndex = allocateBlock();
                if (blockIndex != -1) {
                    allocatedBlocks.add(blockIndex);
                } else {
                    System.out.println("Nema dovoljno slobodnih blokova za alokaciju.");
                    break;
                }
            }
            return allocatedBlocks;
        }


        public void freeBlocks(List<Integer> blockIndices) {
            freeBlocks.addAll(blockIndices);
        }

        public void changeDirectory(String name) {
            if (name.equals("/")) {
                currentDir = root;
            } else if (name.equals("..")) {
                if (currentDir.getParent() != null) {
                    currentDir = currentDir.getParent();
                }
            } else {
                Directory newDir = currentDir.findSubDirectory(name);
                if (newDir != null) {
                    currentDir = newDir;
                } else {
                    System.out.println("Direktorijum ne postoji.");
                }
            }
            System.out.println("Promjenjen direktorijum na " + getCurrentDirPath());
        }

        public void handleBackCommand() {
            if (currentDir.getParent() != null) {
                currentDir = currentDir.getParent();
                System.out.println("Promjenjen direktorijum na " + getCurrentDirPath());
            } else {
                System.out.println("Vec ste u korjenom direktorijumu");
            }
        }

        public void listCurrentDirectory() {
            System.out.println("Listing of directory: " + getCurrentDirPath());
            for (Directory dir : currentDir.getSubDirectories()) {
                System.out.println("[DIR] " + dir.getName());
            }
            for (File file : currentDir.getFiles()) {
                System.out.println(file.getName());
            }
        }

        public void createDirectory(String name) {
            Directory newDir = new Directory(Paths.get(currentDir.getAbsolutePath(), name).toString(), currentDir);
            currentDir.addSubDirectory(newDir);
            java.io.File dir = new java.io.File(newDir.getAbsolutePath());
            if (!dir.exists()) {
                dir.mkdir();
            }
            System.out.println("Kreiran direktorijum: " + name);
        }


        public void delete(String name) {
            Directory dirToDelete = currentDir.findSubDirectory(name);
            if (dirToDelete != null) {
                currentDir.getSubDirectories().remove(dirToDelete);
                java.io.File dir = new java.io.File(dirToDelete.getAbsolutePath());
                if (dir.exists()) {
                    try {
                        Files.delete(Paths.get(dir.getAbsolutePath()));
                    } catch (IOException e) {
                        System.out.println("Neuspjesno brisanje: " + name);
                    }
                }
                System.out.println("Obrisan direktorijum: " + name);
                return;
            }

            File fileToDelete = currentDir.findFile(name);
            if (fileToDelete != null) {
                currentDir.getFiles().remove(fileToDelete);
                java.io.File file = new java.io.File(Paths.get(currentDir.getAbsolutePath(), name).toString());
                if (file.exists()) {
                    try {
                        Files.delete(Paths.get(file.getAbsolutePath()));
                        freeBlocks(fileToDelete.getBlockIndices());
                    } catch (IOException e) {
                        System.out.println("Neuspjesno brisanje: " + name);
                    }
                }
                System.out.println("Obrisan fajl: " + name);
                return;
            }

            System.out.println("Fajl/Direktorijum sa takvim nazivom ne postoji.");
        }

        public String getCurrentDirPath() {
            return currentDir.getAbsolutePath();
        }


        public void showFreeBlocks() {
            System.out.println("Slobodni blokovi: " + freeBlocks);
        }


        public boolean hasSufficientFreeBlocks(int requiredBlocks) {
            return freeBlocks.size() >= requiredBlocks;
        }
    }

