import subprocess
import json
import os
import base64
from datetime import datetime

def update_home_navigation_history(action_type):
    """Update home navigation history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        home_nav_record = {
            "id": f"home_nav_{timestamp}",
            "action": action_type,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "destination": "家",
            "page": "Home Navigation",
            "success": True
        }
        
        device_file_path = 'files/7_home_navigation_history.json'
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
        
        history_records.append(home_nav_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_home_nav7.json')
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
                print(f"Home navigation history updated to device storage: {action_type}")
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

def HomeNavigationCheck():
    """Check if home button was clicked on homepage"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check if "回家" button was clicked - look for route planning or navigation started
            home_click_indicators = [
                '回家', '路线规划', '导航', '开始导航',
                '路线', '家', '目的地'
            ]
            
            found_indicators = [indicator for indicator in home_click_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_home_navigation_history("Click Go Home Button"):
                    return True
                else:
                    print("X Home navigation history update failed")
                    return False
            else:
                print("X Go home button click not detected in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Home button click detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Click Go Home Button")
    result = HomeNavigationCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")