
import exceptions.SkiTypeAlreadyPresent;
import exceptions.SkiTypeNotPresent;
import wit.io.data.SkiType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main
{
    public static void main(String[] args)
    {

        try
        {
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(""));
        } catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }


        try
        {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(""));
        } catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }

    }
}