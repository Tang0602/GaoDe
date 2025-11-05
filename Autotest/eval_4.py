import subprocess
import json
import os
import base64
from datetime import datetime

def update_home_navigation_history(navigation_action):
    """Update home page navigation history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        home_record = {
            "id": f"home_{timestamp}",
            "action": navigation_action,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Home Page",
            "success": True
        }
        
        device_file_path = 'files/4_home_navigation_history.json'
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
        
        history_records.append(home_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_home.json')
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
                print(f"Home navigation history updated to device storage: {navigation_action}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update home navigation history: {e}")
        return False

def HomePageNavigationCheck():
    """Check if successfully returned to home page"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for actual Chinese UI elements in home page
            home_indicators = [
                '武汉站', '公交', '地铁', '骑行', 
                '打车', '步行', '订酒店', '回家', '去单位'
            ]
            
            found_indicators = [indicator for indicator in home_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found home page elements in UI")
                if update_home_navigation_history("Return to Home"):
                    return True
                else:
                    print("X Home navigation history update failed")
                    return False
            else:
                print("X Home page elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Home page navigation detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Return to Home Page")
    result = HomePageNavigationCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")