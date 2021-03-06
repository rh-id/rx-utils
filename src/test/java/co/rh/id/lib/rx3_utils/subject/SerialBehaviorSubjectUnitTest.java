package co.rh.id.lib.rx3_utils.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerialBehaviorSubjectUnitTest {
    @SuppressWarnings("unchecked")
    @Test
    public void serialize() throws IOException, ClassNotFoundException {
        String test = "this is a test";
        SerialBehaviorSubject<String> stringSerialBehaviorSubject = new SerialBehaviorSubject<>(test);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(stringSerialBehaviorSubject);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(bis);
        SerialBehaviorSubject<String> serializedValue = (SerialBehaviorSubject<String>) objectInputStream.readObject();

        assertEquals(stringSerialBehaviorSubject.getValue(), serializedValue.getValue());
    }
}