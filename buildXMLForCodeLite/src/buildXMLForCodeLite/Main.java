package buildXMLForCodeLite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
	static class Entry{
		public String name;
		public boolean dir;
		public List<Entry> files;
		public Entry(String name,boolean dir){
			this.name=name;
			this.dir=dir;
			if(dir)
				this.files=new ArrayList<Entry>();
		}
		
		public Entry addEntry(String name,boolean dir){
			for(Entry e:files){
				if(e.name.equals(name))
					return e;
			}
			Entry e=new Entry(name,dir);
			files.add(e);
			return e;
		}
		public String toString(){
			StringBuilder sb=new StringBuilder();
			if (dir)
			{
				sb.append(String.format("<VirtualDirectory Name=\"%s\">\n",name));
				for(Entry e:files)
					sb.append(e.toString());
				sb.append("</VirtualDirectory>\n");
			}else{
				sb.append(String.format("<File Name=\"%s\"/>\n",name));
			}
			return sb.toString();
		}
	}
	static Entry ret=new Entry("src",true);
	
	static HashMap<String,List<String>> res=new HashMap<String,List<String>>();
	static List<String> types=new ArrayList<String>();
	public static void main(String[] args) throws Exception {
		String path="/Users/user/Desktop/proj/qc2d/trunk/quick-cocos2d-x/lib/qc2d";
		if(args.length==0)
		{
			System.out.println("Usage: PROG PATH ext1 ext2 ");
			System.exit(0);
		}

		if(args.length>0)
			path=args[0];
		if(args.length>1)
		{
			for(int i=1;i<args.length;++i)
				types.add(args[i]);
		}
		File root=new File(path);
		listAll(root);
		
		
		for(String k :res.keySet()){
			List<String> l=res.get(k);

			k=k.substring(path.length());
			if(k.startsWith(File.separator))
				k=k.substring(1);
			String[] dirs=k.split(File.separator);
			Entry cEntry=ret;
			for(String dir:dirs){
				if(dir.length()==0)
					continue;
				cEntry=cEntry.addEntry(dir, true);
			}
			for(String s:l)
			{
				String p=k;
				if(p.length()>0)
				{
					p=p+File.separator;
				}
				cEntry.addEntry(p+s, false);
//				System.out.println(k+File.separator+s);
			}
		}
		String s=ret.toString();
		BufferedWriter br=new BufferedWriter(new FileWriter(new File(root.getAbsolutePath()+File.separator+"files.xml")));
		br.write(s);
		br.close();
		return;
	}
	
	private static void listAll(File f)
	{
		if(f.isDirectory()){
			File[] files=f.listFiles();
			for(File f1:files)
				listAll(f1);
		}else{
			String name=f.getName();
			if(types.size()>0){
				String extension=null;
				int i = name.lastIndexOf('.');
				if (i > 0) {
				    extension = name.substring(i+1);
				}
				if(extension!=null && extension.length()>0 && !types.contains(extension))
					return;
			}
			String k=f.getParent();
			List<String> l=res.get(k);
			if(l==null){
				l=new ArrayList<String>();
				res.put(k, l);
			}
			l.add(f.getName());
		}
	}

}
