import subprocess
import json
import os
import base64
from datetime import datetime

def update_ride_history(action_type):
    """Update ride to Donghu Scenic Area history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        ride_record = {
            "id": f"ride_{timestamp}",
            "action": action_type,
            "destination": "东湖风景区",
            "rideType": "打车",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Ride to Donghu",
            "success": True
        }
        
        device_file_path = 'files/9_donghu_ride_history.json'
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
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_donghu_ride9.json')
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
                print(f"Donghu ride history updated to device storage: {action_type}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update Donghu ride history: {e}")
        return False

def DonghuRideCheck():
    """Check if ride to Donghu Scenic Area is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for ride to Donghu elements
            donghu_ride_indicators = [
                '东湖风景区', '打车', '叫车', '快车',
                '景区', '东湖', '确认用车', '出发'
            ]
            
            found_indicators = [indicator for indicator in donghu_ride_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_ride_history("Ride to Donghu Scenic Area"):
                    return True
                else:
                    print("X Donghu ride history update failed")
                    return False
            else:
                print("X Donghu ride elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Donghu ride detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Ride to Donghu Scenic Area")
    result = DonghuRideCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")