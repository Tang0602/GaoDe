import subprocess
import json
import os
import base64
from datetime import datetime

def update_skin_history(action_type):
    """Update skin purchase and change history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        skin_record = {
            "id": f"skin_{timestamp}",
            "action": action_type,
            "skinName": "美乐蒂皮肤",
            "skinType": "卡通主题",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Skin Purchase",
            "success": True
        }
        
        device_file_path = 'files/16_skin_history.json'
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
        
        history_records.append(skin_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_skin16.json')
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
                print(f"Skin history updated to device storage: {action_type}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update skin history: {e}")
        return False

def SkinPurchaseCheck():
    """Check if Melody skin purchase and change is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for skin purchase and change elements
            skin_indicators = [
                '美乐蒂', '皮肤', '购买', '更换',
                '我的皮肤', '主题', '卡通', '应用皮肤'
            ]
            
            found_indicators = [indicator for indicator in skin_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_skin_history("Purchase and Apply Melody Skin"):
                    return True
                else:
                    print("X Skin history update failed")
                    return False
            else:
                print("X Skin purchase elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Skin purchase detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Purchase and Apply Melody Skin")
    result = SkinPurchaseCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")