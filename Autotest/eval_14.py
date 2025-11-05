import subprocess
import json
import os
import base64
from datetime import datetime

def update_share_history(action_type):
    """Update hotel location sharing history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        share_record = {
            "id": f"share_{timestamp}",
            "action": action_type,
            "hotelName": "如家酒店",
            "sharedTo": "妈妈",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Share Hotel Location",
            "success": True
        }
        
        device_file_path = 'files/14_hotel_share_history.json'
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
        
        history_records.append(share_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_hotel_share14.json')
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
                print(f"Hotel share history updated to device storage: {action_type}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update hotel share history: {e}")
        return False

def HotelShareCheck():
    """Check if hotel location sharing to mom is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for hotel sharing elements
            share_indicators = [
                '如家酒店', '分享', '妈妈', '位置',
                '分享位置', '发送', '分享给', '位置信息'
            ]
            
            found_indicators = [indicator for indicator in share_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_share_history("Share Rujia Hotel Location to Mom"):
                    return True
                else:
                    print("X Hotel share history update failed")
                    return False
            else:
                print("X Hotel sharing elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Hotel sharing detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Share Rujia Hotel Location to Mom")
    result = HotelShareCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")