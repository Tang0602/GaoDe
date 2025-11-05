import subprocess
import json
import os
import base64
from datetime import datetime

def update_navigation_history(action_type):
    """Update navigation to Muyu BBQ with public transport history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        navigation_record = {
            "id": f"muyu_nav_{timestamp}",
            "action": action_type,
            "destination": "木屋烧烤（光谷世界城店）",
            "transportMode": "公共交通",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Public Transport Navigation",
            "success": True
        }
        
        device_file_path = 'files/15_muyu_navigation_history.json'
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
        
        history_records.append(navigation_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_muyu_nav15.json')
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
                print(f"Muyu navigation history updated to device storage: {action_type}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update Muyu navigation history: {e}")
        return False

def MuyuNavigationCheck():
    """Check if navigation to Muyu BBQ with public transport is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for Muyu BBQ public transport navigation elements
            navigation_indicators = [
                '木屋烧烤', '光谷世界城店', '公共交通', '地铁',
                '公交', '导航', '路线规划', '公共出行'
            ]
            
            found_indicators = [indicator for indicator in navigation_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_navigation_history("Navigate to Muyu BBQ via Public Transport"):
                    return True
                else:
                    print("X Muyu navigation history update failed")
                    return False
            else:
                print("X Muyu public transport navigation elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Muyu navigation detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Navigate to Muyu BBQ via Public Transport")
    result = MuyuNavigationCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")