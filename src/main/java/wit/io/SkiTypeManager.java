package wit.io;

import wit.io.data.SkiType;
import java.io.*;


// TODO: SWING
public class SkiTypeManager extends Manager<SkiType>{
    public SkiTypeManager(String filePath) {
        super(filePath);
    }

    @Override
    public void writeToFile() {
        // writes skiTypes to file (override)
        try (DataOutputStream output =
                 new DataOutputStream(new FileOutputStream(filePath))) {

            output.writeInt(dataEntity.size());
            for (SkiType type : dataEntity) {
                output.writeUTF(type.getName());
                output.writeUTF(type.getDescription());
            }

        } catch (IOException e) {
            // TODO: do something here
        }

    }

    @Override
    public void readFromFile() {
        // at the beggining
        try (DataInputStream input =
                     new DataInputStream(new FileInputStream(filePath))) {
            int length = input.readInt();
            for (int i = 0; i < length; i++) {
                String name = input.readUTF();
                String description = input.readUTF();
                SkiType skiType = new SkiType(name, description);
                System.out.println(skiType.getName());
                System.out.println(skiType.getDescription());
                dataEntity.add(skiType);
            }

        }catch(IOException e){
            // TODO: do something here
        }
    }
}
