import subprocess
import json
import os
from datetime import datetime

def update_hotel_booking_history(action_type, hotel_name):
    """更新汉庭酒店预订历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        booking_record = {
            "id": f"hotel_booking_{timestamp}",
            "action": action_type,
            "hotelName": hotel_name,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "bookingStatus": "支付成功",
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '14_汉庭酒店预订历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(booking_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 汉庭酒店预订历史记录已更新: {action_type}")
        return True
        
    except Exception as e:
        print(f"更新汉庭酒店预订历史失败: {e}")
        return False

def HantingHotelBookingCheck():
    """检测是否成功预订汉庭酒店"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            booking_indicators = [
                '汉庭酒店', '订购', '支付成功', '预订成功', '确认',
                '经济型', '近地铁', '24小时前台', '免费WIFI'
            ]
            
            found_indicators = [indicator for indicator in booking_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到汉庭酒店预订元素: {', '.join(found_indicators)}")
                update_hotel_booking_history("预订汉庭酒店", "汉庭酒店")
                return True
            else:
                print("✗ 未在UI中找到汉庭酒店预订元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"汉庭酒店预订检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：预订汉庭酒店")
    result = HantingHotelBookingCheck()
    print(f"检测结果: {'通过' if result else '失败'}")