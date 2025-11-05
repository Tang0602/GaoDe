import subprocess
import json
import os
import base64
from datetime import datetime

def update_hotel_booking_history(action_type):
    """Update Hanting hotel booking history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        booking_record = {
            "id": f"hotel_booking_{timestamp}",
            "action": action_type,
            "hotelName": "汉庭酒店",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "bookingStatus": "支付成功",
            "page": "Hotel Booking",
            "success": True
        }
        
        device_file_path = 'files/10_hanting_booking_history.json'
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
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_hanting10.json')
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
                print(f"Hanting booking history updated to device storage: {action_type}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update Hanting booking history: {e}")
        return False

def HantingHotelBookingCheck():
    """Check if Hanting hotel booking is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for Hanting hotel booking elements
            booking_indicators = [
                '汉庭酒店', '订购', '支付成功', '预订成功', '确认',
                '经济型', '近地铁', '24小时前台', '免费WIFI'
            ]
            
            found_indicators = [indicator for indicator in booking_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_hotel_booking_history("Book Hanting Hotel"):
                    return True
                else:
                    print("X Hanting booking history update failed")
                    return False
            else:
                print("X Hanting hotel booking elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Hanting hotel booking detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Book Hanting Hotel")
    result = HantingHotelBookingCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")