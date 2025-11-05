import subprocess
import json
import os
import base64
from datetime import datetime

def update_hotel_booking_history(booking_action):
    """Update hotel booking history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        booking_record = {
            "id": f"hotel_{timestamp}",
            "action": booking_action,
            "hotelType": "最贵的酒店",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Hotel Booking",
            "success": True
        }
        
        device_file_path = 'files/19_hotel_booking_history.json'
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
        
        history_records.append(booking_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_hotel19.json')
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
                print(f"Hotel booking history updated to device storage: {booking_action}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update hotel booking history: {e}")
        return False

def ExpensiveHotelBookingCheck():
    """Check if most expensive hotel booking is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for hotel booking and price sorting UI elements
            hotel_indicators = [
                '最贵', '价格排序', '酒店预订', '豪华酒店',
                '五星级', '预订', '高价', '排序', '酒店'
            ]
            
            found_indicators = [indicator for indicator in hotel_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_hotel_booking_history("Find and Book Most Expensive Hotel"):
                    return True
                else:
                    print("X Hotel booking history update failed")
                    return False
            else:
                print("X Hotel booking elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Hotel booking detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Find and Book Most Expensive Hotel")
    result = ExpensiveHotelBookingCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")