package by.hzv.s2.adapter.dctm;

import java.math.BigInteger;

import org.apache.commons.io.FilenameUtils;

import by.hzv.s2.model.FileInfo;
import by.hzv.s2.model.SimpleFileInfo;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.common.DfException;
import com.google.common.base.Function;

/**
 * @author <a href="mailto:mityan@wiley.com">Mikhail Tyan</a>
 * Function transforms IDFDocument to FileInfo
 */
public class IdfToFileInfoMapper implements Function<IDfDocument, FileInfo> {

    @Override
    public FileInfo apply(IDfDocument doc) {
        try {
            String flid = doc.getObjectId().getId();
            String filename = doc.getObjectName();
            String extension = FilenameUtils.getExtension(filename);
            BigInteger size = BigInteger.valueOf(doc.getContentSize());
            return new SimpleFileInfo(flid, filename, extension, size);
        } catch (DfException e) {
            throw new RuntimeException("Documentum object exception.", e);
        }
    }
}