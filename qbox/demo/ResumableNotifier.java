import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;



import org.json.JSONObject;
import org.json.JSONStringer;

import com.qiniu.qbox.up.BlockProgress;
import com.qiniu.qbox.up.BlockProgressNotifier;
import com.qiniu.qbox.up.ProgressNotifier;


public class ResumableNotifier implements ProgressNotifier, BlockProgressNotifier {

	private PrintStream os;
	
	public ResumableNotifier(String progressFile) throws Exception {
		OutputStream out = new FileOutputStream(progressFile, true);
		this.os = new PrintStream(out, true);
	}
	
	@Override
	public void notify(int blockIndex, String checksum) {
	
		try {
			HashMap<String, Object> doc = new HashMap<String, Object>();
			doc.put("block", blockIndex);
			doc.put("checksum", checksum);
			String json = JSONObject.valueToString(doc);
			os.println(json);
			System.out.println("Progress Notify:" +
					"\n\tBlockIndex: " + String.valueOf(blockIndex) + 
					"\n\tChecksum: " + checksum);
		} catch (Exception e) {
			// nothing to do;
		}
	}

	@Override
	public void notify(int blockIndex, BlockProgress progress) {

		try {
			HashMap<String, Object> doc = new HashMap<String, Object>();
			doc.put("block", blockIndex);

			
			Map<String,String> map = new HashMap<String,String>();
			map.put("context", progress.context);
			map.put("offset", progress.offset + "");
			map.put("restSize", progress.restSize + "");
			doc.put("progress",map);
			
			
			/*
			JSONStringer stringer = new JSONStringer(); 
			stringer.object();
			stringer.key("context").value(progress.context);
			stringer.key("offset").value(progress.offset);
			stringer.key("restSize").value(progress.restSize);
			stringer.endObject(); 
			doc.put("progress", stringer.toString());
			*/
			String json = JSONObject.valueToString(doc);
			os.println(json);
			System.out.println("BlockProgress Notify:" +
					"\n\tBlockIndex: " + String.valueOf(blockIndex) + 
					"\n\tContext: " + progress.context +
					"\n\tOffset: " + String.valueOf(progress.offset) +
					"\n\tRestSize: " + String.valueOf(progress.restSize));
		} catch (Exception e) {
			// nothing to do;
		}
	}
}
