import subprocess
import json
import os
from datetime import datetime

def update_order_history(action_type):
    """更新订单查看历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        order_record = {
            "id": f"order_{timestamp}",
            "action": action_type,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "订单列表",
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '5_订单历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(order_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 订单历史记录已更新: {action_type}")
        return True
        
    except Exception as e:
        print(f"更新订单历史失败: {e}")
        return False

def OrderListCheck():
    """检测是否成功查看订单列表"""
    try:
        # 检查UI中是否存在订单列表页面元素
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            # 检查订单列表页面特有的UI元素
            order_indicators = [
                '我的订单', '全部', '已完成', '进行中', '已取消',
                '打车', '酒店', '加油', '代驾', '门票旅游',
                '及时特选经济型', '风韵特选经济型', '聚的出租车',
                '删除', '开发票', '呼叫返程', '再来一单'
            ]
            
            found_indicators = [indicator for indicator in order_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到订单列表元素: {', '.join(found_indicators)}")
                update_order_history("查看我的订单列表")
                return True
            else:
                print("✗ 未在UI中找到订单列表元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"订单列表检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：查看我的订单列表")
    result = OrderListCheck()
    print(f"检测结果: {'通过' if result else '失败'}")