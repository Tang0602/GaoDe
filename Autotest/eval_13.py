import subprocess
import json
import os
import base64
from datetime import datetime

def update_ride_history(action_type):
    """Update ride to Wuhan Happy Valley history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        ride_record = {
            "id": f"happy_valley_ride_{timestamp}",
            "action": action_type,
            "destination": "武汉欢乐谷",
            "rideType": "经济型打车",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Ride to Happy Valley",
            "success": True
        }
        
        device_file_path = 'files/13_happy_valley_ride_history.json'
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
        
        history_records.append(ride_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_happy_valley13.json')
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
                print(f"Happy Valley ride history updated to device storage: {action_type}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update Happy Valley ride history: {e}")
        return False

def HappyValleyRideCheck():
    """Check if ride to Happy Valley with economy car is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for Happy Valley ride with economy car elements
            ride_indicators = [
                '武汉欢乐谷', '经济型', '打车', '叫车',
                '欢乐谷', '经济', '确认用车', '快车'
            ]
            
            found_indicators = [indicator for indicator in ride_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_ride_history("Ride to Happy Valley with Economy Car"):
                    return True
                else:
                    print("X Happy Valley ride history update failed")
                    return False
            else:
                print("X Happy Valley economy ride elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Happy Valley ride detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Ride to Happy Valley with Economy Car")
    result = HappyValleyRideCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")