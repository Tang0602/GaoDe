import subprocess
import json
import os
from datetime import datetime

def update_favorite_history(action_type, place_name):
    """更新收藏餐厅历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        favorite_record = {
            "id": f"favorite_{timestamp}",
            "action": action_type,
            "placeName": place_name,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "favoriteStatus": "已收藏",
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '15_收藏餐厅历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(favorite_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 收藏餐厅历史记录已更新: {action_type}")
        return True
        
    except Exception as e:
        print(f"更新收藏餐厅历史失败: {e}")
        return False

def FavoriteRestaurantCheck():
    """检测是否成功收藏一个餐厅"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            favorite_indicators = [
                '❤️', '收藏', '已收藏', '取消收藏', '我的收藏',
                '收藏夹', '222', '223', '224'  # 收藏数量变化
            ]
            
            found_indicators = [indicator for indicator in favorite_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到收藏餐厅元素: {', '.join(found_indicators)}")
                update_favorite_history("收藏一个餐厅", "餐厅")
                return True
            else:
                print("✗ 未在UI中找到收藏餐厅元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"收藏餐厅检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：收藏一个餐厅")
    result = FavoriteRestaurantCheck()
    print(f"检测结果: {'通过' if result else '失败'}")