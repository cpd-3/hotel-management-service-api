package com.cpd.hotel_system.hotel_management_service_api.util;

import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;

@Service
public class FileDataExtractor {
    public CommonFileSavedSimpleDataDTO toStringDataObject(CommonFileSavedBinaryDataDTO dto){
        try {
            String hash;
            String fileName;
            String resourceUrl;
            StringBuffer buf = new StringBuffer();
            String temp;
            BufferedReader bufReader = null;
            bufReader = new BufferedReader(new InputStreamReader(dto.getHash().getBinaryStream()));
            while ((temp = bufReader.readLine()) != null) {
                buf.append(temp);
            }
            hash= buf.toString();
            buf = new StringBuffer();
            bufReader = new BufferedReader(new InputStreamReader(dto.getFileName().getBinaryStream()));
            while ((temp = bufReader.readLine()) != null) {
                buf.append(temp);
            }
            fileName= buf.toString();
            buf = new StringBuffer();
            bufReader = new BufferedReader(new InputStreamReader(dto.getResourceUrl().getBinaryStream()));
            while ((temp = bufReader.readLine()) != null) {
                buf.append(temp);
            }
            resourceUrl= buf.toString();
            buf = new StringBuffer();
            return new CommonFileSavedSimpleDataDTO(hash,dto.getDirectory(),fileName,resourceUrl);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public String extractActualFileName(InputStreamReader data){
        try {
            StringBuffer buf = new StringBuffer();
            String temp;
            BufferedReader bufReader = null;
            buf = new StringBuffer();
            bufReader = new BufferedReader(data);
            while ((temp = bufReader.readLine()) != null) {
                buf.append(temp);
            }
            return buf.toString();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public String stringToBinary(String input) {
        if (input == null || input.isEmpty()) {
            return "Input string is empty";
        }

        StringBuilder binaryStringBuilder = new StringBuilder();

        for (char c : input.toCharArray()) {
            String binary = Integer.toBinaryString(c);
            binaryStringBuilder.append(binary).append(" ");
        }

        return binaryStringBuilder.toString().trim();
    }
    public byte[] blobToByteArray(Blob blob) throws SQLException, IOException {
        if (blob == null) {
            return new byte[0];
        }

        try (InputStream inputStream = blob.getBinaryStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }
    public Blob byteArrayToBlob(byte[] byteArray) throws SQLException {
        if (byteArray == null || byteArray.length == 0) {
            return null;
        }

        return new SerialBlob(byteArray);
    }
    public String byteArrayToString(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) {
            return null;
        }

        return new String(byteArray, StandardCharsets.UTF_8); // Change the charset as per your requirement
    }
}
