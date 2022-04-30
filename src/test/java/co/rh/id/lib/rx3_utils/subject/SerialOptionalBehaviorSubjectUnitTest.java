package co.rh.id.lib.rx3_utils.subject;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SerialOptionalBehaviorSubjectUnitTest {
    @SuppressWarnings("unchecked")
    @Test
    public void serialize() throws IOException, ClassNotFoundException {
        String test = "this is a test";
        SerialOptionalBehaviorSubject<String> stringSerialBehaviorSubject = new SerialOptionalBehaviorSubject<>(test);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(stringSerialBehaviorSubject);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(bis);
        SerialOptionalBehaviorSubject<String> serializedValue = (SerialOptionalBehaviorSubject<String>) objectInputStream.readObject();

        assertEquals(stringSerialBehaviorSubject.getValue().get(), serializedValue.getValue().get());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void serialize_empty() throws IOException, ClassNotFoundException {
        SerialOptionalBehaviorSubject<String> stringSerialBehaviorSubject = new SerialOptionalBehaviorSubject<>();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(stringSerialBehaviorSubject);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(bis);
        SerialOptionalBehaviorSubject<String> serializedValue = (SerialOptionalBehaviorSubject<String>) objectInputStream.readObject();

        assertFalse(serializedValue.getValue().isPresent());
    }
}