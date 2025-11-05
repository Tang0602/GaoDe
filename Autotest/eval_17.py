import subprocess
import json
import os
import base64
from datetime import datetime

def update_navigation_history(navigation_action):
    """Update navigation history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        navigation_record = {
            "id": f"nav_{timestamp}",
            "action": navigation_action,
            "destination": "南湖花亭公园",
            "routeType": "最优时间",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Navigation",
            "success": True
        }
        
        device_file_path = 'files/17_navigation_history.json'
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
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_navigation17.json')
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
                print(f"Navigation history updated to device storage: {navigation_action}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update navigation history: {e}")
        return False

def NavigationToNanhuCheck():
    """Check if clicked on public transport 12-minute optimal time route"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for destination, public transport, and time elements (more flexible matching)
            has_destination = any(dest in ui_dump for dest in ['南湖花亭公园', '南湖', '花亭公园'])
            has_public_transport = any(transport in ui_dump for transport in ['公共交通', '公交', '地铁'])
            has_time_route = any(time in ui_dump for time in ['12分钟', '12 分钟', '12min', '最优时间'])
            
            if has_destination and has_public_transport and has_time_route:
                print(f"SUCCESS: Found UI elements")
                if update_navigation_history("Navigate to Nanhu Huating Park - Optimal Time Route"):
                    return True
                else:
                    print("X Navigation history update failed")
                    return False
            else:
                if not has_destination:
                    print("X Destination not found in UI")
                elif not has_public_transport:
                    print("X Public transport option not found in UI")
                else:
                    print("X Optimal time route not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Navigation detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Navigate to Nanhu Huating Park - Optimal Time Route")
    result = NavigationToNanhuCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")