import com.amazonaws.services.lambda.runtime.*;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DocToPdfConversion implements RequestStreamHandler {


    public void handleRequest(InputStream in, OutputStream outputStream, Context context) throws IOException {
        IConverter converter = LocalConverter.builder()
                .baseFolder(new File(System.getProperty("user.dir") + File.separator +"test"))
                .workerPool(20, 25, 2, TimeUnit.SECONDS)
                .processTimeout(5, TimeUnit.SECONDS)
                .build();

        ByteArrayOutputStream bo = new ByteArrayOutputStream();

        Future<Boolean> conversion = converter
                .convert(in).as(DocumentType.DOC)
                .to(bo).as(DocumentType.PDF)
                .prioritizeWith(1000) // optional
                .schedule();
        try {
            conversion.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        bo.writeTo(outputStream);
        in.close();
        bo.close();

    }
}
