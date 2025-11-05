import subprocess
import json
import os
import base64
from datetime import datetime

def update_chat_history(action_type):
    """Update chat with dad history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        chat_record = {
            "id": f"dad_chat_{timestamp}",
            "action": action_type,
            "contactId": "dad",
            "contactName": "爸爸",
            "message": "吃了吗",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Chat with Dad",
            "success": True
        }
        
        device_file_path = 'files/12_dad_chat_history.json'
        history_records = []
        
        try:
            result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 'cat', device_file_path],
                                   stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding='utf-8', errors='ignore')
            if result.returncode == 0 and result.stdout and result.stdout.strip():
                try:
                    history_records = json.loads(result.stdout)
                except json.JSONDecodeError:
                    history_records = []
        except Exception:
            history_records = []
        
        history_records.append(chat_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_dad_chat12.json')
        with open(temp_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        with open(temp_file, 'r', encoding='utf-8') as f:
            json_content = f.read()
        
        json_bytes = json_content.encode('utf-8')
        json_b64 = base64.b64encode(json_bytes).decode('ascii')
        
        create_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                      'sh', '-c', f'echo "{json_b64}" | base64 -d > {device_file_path}'],
                                     capture_output=True, text=True)
        
        os.remove(temp_file)
        
        if create_result.returncode == 0:
            verify_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                          'test', '-f', device_file_path],
                                         capture_output=True, text=True)
            
            if verify_result.returncode == 0:
                print(f"Dad chat history updated to device storage: {action_type}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update dad chat history: {e}")
        return False

def DadChatCheck():
    """Check if chat with dad was initiated successfully"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for dad chat initiation elements
            dad_chat_indicators = [
                '爸爸', '吃了吗', '发送', '聊天',
                '消息发送', '对话', '发起聊天', '聊天界面'
            ]
            
            found_indicators = [indicator for indicator in dad_chat_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_chat_history("Initiate Chat with Dad"):
                    return True
                else:
                    print("X Dad chat history update failed")
                    return False
            else:
                print("X Dad chat elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Dad chat detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Initiate Chat with Dad")
    result = DadChatCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")