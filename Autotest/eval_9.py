import subprocess
import json
import os
from datetime import datetime

def update_hotel_search_history(search_query):
    """更新酒店搜索历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        search_record = {
            "id": f"hotel_search_{timestamp}",
            "query": search_query,
            "category": "酒店",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "resultCount": 3,
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '9_酒店搜索历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(search_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 酒店搜索历史记录已更新: {search_query}")
        return True
        
    except Exception as e:
        print(f"更新酒店搜索历史失败: {e}")
        return False

def HotelSearchCheck():
    """检测是否成功查看酒店列表"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            hotel_indicators = [
                '汉庭酒店', '如家酒店', '凯悦酒店', '经济型', '商务型',
                '奢华型', '订购', '价格详询', '免费WIFI', '24小时前台'
            ]
            
            found_indicators = [indicator for indicator in hotel_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到酒店列表元素: {', '.join(found_indicators)}")
                update_hotel_search_history("酒店")
                return True
            else:
                print("✗ 未在UI中找到酒店列表元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"酒店列表检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：查看酒店列表")
    result = HotelSearchCheck()
    print(f"检测结果: {'通过' if result else '失败'}")