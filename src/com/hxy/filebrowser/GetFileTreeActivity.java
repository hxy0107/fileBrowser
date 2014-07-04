package com.hxy.filebrowser;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class GetFileTreeActivity extends Activity {
	ListView list;
	RelativeLayout newNameView;
	EditText newName;
	Button finishButton;
	Button cancelButton;
	String[] menus;
	String exec;
	//illegal string for filename
	String[] x=new String[]{"\\","//",":","*","?","\"","<",">","|" };
	
	
	
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog);
		menus=getIntent().getStringArrayExtra("menus");
		if(menus.length>0){
			list=(ListView)findViewById(R.id.list_dialog_list);
			newNameView=(RelativeLayout)findViewById(R.id.text_dialog_listitem);
			newName=(EditText)findViewById(R.id.edit_dialog_newname);
			cancelButton=(Button)findViewById(R.id.button_dialog_cancel);
			finishButton=(Button)findViewById(R.id.button_dialog_finish);
			finishButton.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.button_dialog_finish:
						String name=newName.getText().toString()+"";
						if(name.length()>0){
							//replace illegal char
							for(int i=0;i<x.length;i++){
								name.replace(x[i], "");
							}
							Intent intent=new Intent();
							intent.putExtra("exec",exec);
							intent.putExtra("name",name);
							GetFileTreeActivity.this.setResult(RESULT_OK, intent);
							GetFileTreeActivity.this.finish();
						}
						break;
					case R.id.button_dialog_cancel:
						GetFileTreeActivity.this.setResult(RESULT_CANCELED);
						GetFileTreeActivity.this.finish();
						break;

					default:
						break;
					}
					
				}
				
				
			});
			cancelButton.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.button_dialog_finish:
						String name=newName.getText().toString()+"";
						if(name.length()>0){
							//replace illegal char
							for(int i=0;i<x.length;i++){
								name.replace(x[i], "");
							}
							Intent intent=new Intent();
							intent.putExtra("exec",exec);
							intent.putExtra("name",name);
							GetFileTreeActivity.this.setResult(RESULT_OK, intent);
							GetFileTreeActivity.this.finish();
						}
						break;
					case R.id.button_dialog_cancel:
						GetFileTreeActivity.this.setResult(RESULT_CANCELED);
						GetFileTreeActivity.this.finish();
						break;

					default:
						break;
					}
					
				}
				
			});
			
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(GetFileTreeActivity.this,R.layout.dialog_listview,menus);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					exec=menus[arg2];
					if(exec.equals("rename")){
						newNameView.setVisibility(View.VISIBLE);
						list.setVisibility(View.GONE);
					}else{
						Intent intent=new Intent();
						intent.putExtra("exec", exec);
						GetFileTreeActivity.this.setResult(RESULT_OK, intent);
						GetFileTreeActivity.this.finish();
					}
				}
				
			});
			
			
		}
		
		
		 class ButtonOnClick implements android.view.View.OnClickListener{
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.button_dialog_finish:
					String name=newName.getText().toString()+"";
					if(name.length()>0){
						//replace illegal char
						for(int i=0;i<x.length;i++){
							name.replace(x[i], "");
						}
						Intent intent=new Intent();
						intent.putExtra("exec",exec);
						intent.putExtra("name",name);
						GetFileTreeActivity.this.setResult(RESULT_OK, intent);
						GetFileTreeActivity.this.finish();
					}
					break;
				case R.id.button_dialog_cancel:
					GetFileTreeActivity.this.setResult(RESULT_CANCELED);
					GetFileTreeActivity.this.finish();
					break;

				default:
					break;
				}
				
			}
			
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
