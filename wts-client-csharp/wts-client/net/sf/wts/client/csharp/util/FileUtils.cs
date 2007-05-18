using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace net.sf.wts.client.util
{
    public class FileUtils
    {
        public static byte[] ReadFileToArray(string fileName)
        { 
            FileInfo fi = new FileInfo(fileName);
            byte[] data = new byte[fi.Length];
            FileStream fs = fi.Open(FileMode.Open, FileAccess.Read);

            ReadStreamToArray(fs, data);

            return data;
        }

        private static void ReadStreamToArray(Stream stream, byte[] data)
        {
            int offset = 0;
            int remaining = data.Length;
            while (remaining > 0)
            {
                int read = stream.Read(data, offset, remaining);
                if (read <= 0)
                    throw new EndOfStreamException
                        (String.Format("End of stream reached with {0} bytes left to read", remaining));
                remaining -= read;
                offset += read;
            }
        }

        public static void writeByteArrayToFile(string fileName, byte[] array)
        {
            using (BinaryWriter binWriter = new BinaryWriter(File.Open(@fileName, FileMode.Create)))
            {
                binWriter.Write(array);
                binWriter.Flush();
                binWriter.Close();
            }
        }
    }
}