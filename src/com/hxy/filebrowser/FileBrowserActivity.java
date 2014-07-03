package com.hxy.filebrowser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileBrowserActivity extends Activity {
	//file root 
	File sdRoot;
	//all present files
	File[] datas;
	//now file
	File nowFile;
	File longFile;
	ListView listView;
	int lastPoint=0;
	boolean isBack=false;
	MyAdapter adapter;
	//file types
	String[] fileTypes=new String[]{"apk","avi","bat","bin","bmp","chm","css","dat","dll","doc","docx","dos","dvd","gif","html","ifo","inf","iso"
			,"java","jpeg","jpg","log","m4a","mid","mov","movie","mp2","mp2v","mp3","mp4","mpe","mpeg","mpg","pdf","php","png","ppt","pptx","psd","rar","tif","ttf"
			,"txt","wav","wma","wmv","xls","xlsx","xml","xsl","zip"};
	String[] menus=null;
	private String Tag="FileBrowserActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		listView=(ListView)findViewById(R.id.listView1);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				File clickFile=datas[arg2];
				Log.e(Tag,"clickFile="+clickFile.getName());
				if(clickFile.isDirectory()){
					lastPoint=arg2;
					loadFiles(clickFile);
				}else{
					openFile(clickFile);
				}
				
			}
			
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				File clickFile=datas[arg2];
				longFile=clickFile;
				menus=new String[]{"delete","rename"};
				//define intent 
				Intent openMenu=new Intent(FileBrowserActivity.this,GetFileTreeActivity.class);
				openMenu.putExtra("menus1", menus);
				if(clickFile.isDirectory()){
					startActivityForResult(openMenu,1);
				}else{
					startActivityForResult(openMenu,2);
				}
				
				
				return false;
			}
			
		});
		Log.e(Tag,"test error1");
		sdRoot=new File("/sdcard");
		if(sdRoot.exists()){
			loadFiles(sdRoot);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("new");
		menu.add("exit");
		return super.onCreateOptionsMenu(menu);
	}
	
	
	
	
	
	
	private void openFile(File f){
		Intent intent=new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type=getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}
	private void deleteFolder(File path){
		getAllFiles(path);
		//allFiles is a arraylist<>
		while(allFiles.size()!=0){
			for(int i=0;i<allFiles.size();i++){
				File file=allFiles.get(i);
				if(file.isFile()){
					boolean delete=file.delete();
					if(delete){
						allFiles.remove(i);
					}else if(file.isDirectory()){
						try{
							boolean deleted=file.delete();
							if(deleted)
							{	allFiles.remove(i);}
						}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
				}
			}
		
	}
	
	//decide the type for MimeType
	private String getMIMEType(File f){
		String type="";
		String fName=f.getName();
		String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase();
		//decide type result from end
		if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||end.equals("xmf")||
				end.equals("ogg")||end.equals("wav")){
			type="audio";
		}else if(end.equals("3gp")||end.equals("mp4")){
			type="video";
		}else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
				end.endsWith("jpeg")||end.endsWith("bmp")){
			type="image";
		}else if(end.endsWith("apk")){
			type="application/vnd.android.package-archive";
		}else if(end.endsWith("txt")||end.endsWith("java")){
			type="text";
		}else{
			type="*";
		}
		//?
		if(end.endsWith("apk")){}
		else{
			type+="/*";
		}
		
		return type;
	}
	
	
	 private void loadFiles(File directory){
		nowFile=directory;
		setTitle(nowFile.getPath());
		File[] temp=directory.listFiles();
		ArrayList<File> tempFolder=new ArrayList<File>();
		ArrayList<File> tempFile=new ArrayList<File>();
		//add folder and file to list
		for(int i=0;i<temp.length;i++){
			File file=temp[i];
			if(file.isDirectory()){
				tempFolder.add(file);
			}else{
				tempFile.add(file);
			}
		}
		Log.e(Tag,"test error2");
		//sort the list
		Comparator<File> comparator=new MyComparator();
		Collections.sort(tempFolder,comparator);
		//Collections.sort(tempFolder);
		Collections.sort(tempFile,comparator);
		//copy the list to new list
		datas=new File[tempFolder.size()+tempFile.size()];
		//*error result in tempFolder,because tempFolder is arraylist,this must turn to array
		System.arraycopy(tempFolder.toArray(), 0, datas, 0, tempFolder.size());
		System.arraycopy(tempFile.toArray(), 0, datas, tempFolder.size(), tempFile.size());
		
		//add the list to view
		adapter=new MyAdapter(FileBrowserActivity.this);
		listView.setAdapter(adapter);
		
		//?
		if(isBack){
			listView.smoothScrollToPosition(lastPoint);
			lastPoint=0;
			isBack=false;
			
		}
		
		
	}
	 //self comparator
	public class MyComparator implements Comparator<File>{

		@Override
		public int compare(File lhs, File rhs) {
			// TODO Auto-generated method stub
			return lhs.getName().compareTo(rhs.getName());
		}
		
	}
	 
	 
	 
	
	public boolean onKeyUp(int keyCode,KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			String parent=nowFile.getParent();
			if(parent.equals("/")){
				return true;
			}
			isBack=true;
			loadFiles(new File(parent));
			Log.e(Tag,"parent file: "+parent);
			return true;
			default:
				break;
		}
		return super.onKeyUp(keyCode, event);
		
	}
	public boolean onMenuItemSelected(int featureId,MenuItem item){
		if(item.getTitle().toString().equals("exit")){
			FileBrowserActivity.this.finish();
			System.exit(1);
			
		}else if(item.getTitle().toString().endsWith("new")){
			String name=System.currentTimeMillis()+"";
			File file=new File(nowFile.getPath()+"/"+name.substring(name.length()-5));
			try{
				boolean finish=file.createNewFile();
				loadFiles(nowFile);
				if(finish){
					Toast.makeText(this, " new file success ", Toast.LENGTH_SHORT).show();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		return super.onMenuItemSelected(featureId, item);
		
	}
	//activity callback method,defined for longpress
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		
		if(resultCode==RESULT_OK){
			String exec=data.getStringExtra("exec");
			switch(requestCode){
			//director
			case 1:
				if(exec.equals("delete")){
					if(longFile!=null){
						if(longFile.exists()){
							try{
								File file=new File(longFile.getPath());
								deleteFolder(file);
								if(allFiles.size()==0){
									boolean deleted=file.delete();
									if(deleted){
										Log.e(Tag,longFile.getName()+"delete succee");
									}else{
										Log.e(Tag,"left :"+allFiles.size()+"not delete");
									}
								}
								
							}catch(Exception e){
								e.printStackTrace();
							}
						}else if(exec.equals("rename")){
							String path=longFile.getPath();
							//data is intent
							String name=data.getStringExtra("name");
							File newFile=new File(longFile.getParent()+"/"+name);
							//boolean java.io.File.renameTo(File newPath);
							longFile.renameTo(newFile);
						}
					}
				}
				break;
			case 2:
				if(exec.equals("delete")){
					if(longFile!=null){
						if(longFile.exists()){
							try{
								File file=new File(longFile.getPath());
								boolean delete=file.delete();
								if(delete){
									Log.e(Tag,longFile.getName()+"delete success");
								}
							}catch(Exception e){
								e.printStackTrace();
							}
						}else if(exec.equals("rename")){
							String path=longFile.getPath();
							String name=data.getStringExtra("name");
							int index=path.lastIndexOf(".");
							String fileType="";
							if(index!=-1){
								fileType=path.substring(index);
							}
							File newFile=new File(longFile.getParent()+"/"+name+fileType);
							Log.e(Tag,"new name is : "+newFile.getName());
							longFile.renameTo(newFile);
						}
					}
				}
				break;
				default:
					break;
			}
			loadFiles(nowFile);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	//search all the current files,used after review the view
	ArrayList<File> allFiles=new ArrayList<File>();
	
	void getAllFiles(File path){
		File[] files=path.listFiles();
		for(int i=0;i<files.length;i++)
		{
			File file=files[i];
			allFiles.add(file);
			if(file.isDirectory()){
				getAllFiles(file);
			}
		}
		Log.v(Tag,"allFile.size():"+allFiles.size()+" ");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private class MyAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		

		public MyAdapter(Context context){
			
			mInflater=LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return datas[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder mViewHolder;
			if(convertView==null){
				convertView=mInflater.inflate(R.layout.item, null);
			
			mViewHolder=new ViewHolder();
			mViewHolder.typeView=(ImageView)convertView.findViewById(R.id.imageView1);
			mViewHolder.nameView=(TextView)convertView.findViewById(R.id.textView1);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder=(ViewHolder)convertView.getTag();
		}
			File file=datas[position];
			if(file.isDirectory()){
				mViewHolder.typeView.setImageResource(R.drawable.folder);
			}else{
				mViewHolder.typeView.setImageResource(R.drawable.file);
				String name=file.getName();
				int pointIndex=name.lastIndexOf(".");
				Log.v(Tag,"pointIndex:"+pointIndex);
				if(pointIndex!=-1){
					String type=name.substring(pointIndex+1).toLowerCase();
					for(int i=0;i<fileTypes.length;i++){
						if(type==fileTypes[i]){
							try{
								int resId=getResources().getIdentifier(type, "drawable", getPackageName());
								mViewHolder.typeView.setImageResource(resId);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					
				}
				
			}
			
			mViewHolder.nameView.setText(file.getName());
			
			return convertView;
		}
		
	}
	
	class ViewHolder{
		ImageView typeView;
		TextView nameView;
	}

}
