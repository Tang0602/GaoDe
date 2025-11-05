import subprocess
import json
import os
import base64
from datetime import datetime

def update_banu_selection_history(action_type):
    """Update Banu hotpot selection history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        selection_record = {
            "id": f"banu_{timestamp}",
            "action": action_type,
            "placeName": "巴奴毛肚火锅(群光广场店)",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "category": "火锅",
            "rating": "4.5分",
            "price": "¥148/人",
            "page": "Banu Hotpot Selection",
            "success": True
        }
        
        device_file_path = 'files/8_banu_selection_history.json'
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
        
        history_records.append(selection_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_banu8.json')
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
                print(f"Banu selection history updated to device storage: {action_type}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update Banu selection history: {e}")
        return False

def BanuHotpotSelectionCheck():
    """Check if Banu hotpot search and selection is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for Banu hotpot detail page elements
            banu_indicators = [
                '巴奴毛肚火锅', '群光广场店', '4.5超棒', '¥148/人',
                '街道口火锅榜', '第5名', '营业中', '筒香小油条',
                '必点毛肚', '导航', '路线', '收藏'
            ]
            
            found_indicators = [indicator for indicator in banu_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_banu_selection_history("Search and Select Banu Hotpot"):
                    return True
                else:
                    print("X Banu selection history update failed")
                    return False
            else:
                print("X Banu hotpot detail page elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Banu hotpot selection detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Search and Select Banu Hotpot")
    result = BanuHotpotSelectionCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")