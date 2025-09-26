<template>
  <div class="app-container">
    <!-- 左侧角色选择区 -->
    <div class="left-panel">
      <h2>角色库 🔍</h2>
      <el-input
        v-model="query"
        placeholder="搜索角色（例如：哈利波特、苏格拉底）"
        clearable
        size="medium"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-divider>热门角色</el-divider>

      <div
        v-for="c in hotCharacters"
        :key="c.name"
        class="character-card"
        @click="selectCharacter(c)"
      >
        <div class="character-avatar">{{ c.name.slice(0, 2) }}</div>
        <div>
          <div style="font-weight: 600">{{ c.name }}</div>
          <div style="font-size: 12px; color: #666">{{ c.description }}</div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天窗口 -->
    <div class="right-panel">
      <!-- 聊天标题 -->
      <div v-if="currentCharacter" class="chat-header">
        <div class="chat-avatar">{{ currentCharacter.slice(0, 2) }}</div>
        <div>
          <h3 style="margin: 0">{{ currentCharacter }}</h3>
          <div style="font-size: 12px; color: #999">
            当前模式：{{ replyMode === 'text' ? '文字回复' : '语音回复' }}
          </div>
        </div>
        <el-switch
          v-model="replyMode"
          active-value="voice"
          inactive-value="text"
          active-text="语音"
          inactive-text="文字"
          style="margin-left: auto"
        />
      </div>

      <!-- 聊天消息 -->
      <div class="chat-messages" ref="messagesEl">
        <div v-for="(msg, idx) in messages" :key="idx" class="chat-row" :class="msg.role">
          <!-- AI 消息 -->
          <template v-if="msg.role === 'ai'">
            <div class="avatar left">{{ currentCharacter.slice(0, 2) }}</div>
            <div class="bubble ai">{{ msg.text }}</div>
          </template>
          <!-- 用户消息（无头像） -->
          <template v-else>
            <div class="bubble user">{{ msg.text }}</div>
          </template>
        </div>
      </div>

      <!-- 输入区 -->
      <div class="chat-input">
        <el-input
          v-model="input"
          placeholder="请输入消息..."
          size="medium"
          @keyup.enter.native="send"
        />
        <el-button type="primary" @click="send">发送</el-button>
        <el-button type="success" @click="startVoice" :disabled="isListening">
          🎤 {{ isListening ? '听中...' : '语音输入' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const query = ref('')
const input = ref('')
const messages = ref([{ role: 'ai', text: '你好，我是哈利波特！你可以和我文字或语音交流。' }])
const currentCharacter = ref('哈利波特')

// 回复模式：text（文字） / voice（语音）
const replyMode = ref('text')

const hotCharacters = [
  { name: '哈利波特', description: '勇敢的巫师，正义感强' },
  { name: '苏格拉底', description: '古希腊哲学家，善于发问' },
  { name: '钢铁侠', description: '科技天才，幽默自信' },
  { name: '福尔摩斯', description: '推理大师，观察敏锐' },
]

const messagesEl = ref(null)
function scrollToBottom() {
  nextTick(() => {
    if (messagesEl.value) messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  })
}

function selectCharacter(c) {
  currentCharacter.value = c.name
  messages.value = [{ role: 'ai', text: `你好，我是 ${c.name}，现在可以开始和我对话啦！` }]
}

function send() {
  if (!input.value) return
  messages.value.push({ role: 'user', text: input.value })

  // 模拟 AI 回复
  setTimeout(() => {
    const reply = `${currentCharacter.value} 回复: ${input.value}`
    messages.value.push({ role: 'ai', text: reply })
    scrollToBottom()

    // 如果是语音模式，则自动播报
    if (replyMode.value === 'voice') {
      speak(reply)
    }
  }, 500)

  input.value = ''
  scrollToBottom()
}

/* --- 语音输入 --- */
let recognition = null
const isListening = ref(false)

if ('webkitSpeechRecognition' in window) {
  recognition = new webkitSpeechRecognition()
  recognition.lang = 'zh-CN'
  recognition.interimResults = false
  recognition.maxAlternatives = 1

  recognition.onstart = () => {
    isListening.value = true
  }
  recognition.onend = () => {
    isListening.value = false
  }
  recognition.onerror = () => {
    ElMessage.error('语音识别出错或不被支持')
    isListening.value = false
  }
  recognition.onresult = (event) => {
    const text = event.results[0][0].transcript
    input.value = text
    send()
  }
} else {
  ElMessage.warning('当前浏览器不支持语音识别，请使用 Chrome/Edge')
}

function startVoice() {
  if (recognition) recognition.start()
}

/* --- 语音播报（自动说话） --- */
function speak(text) {
  if (!window.speechSynthesis) {
    ElMessage.warning('当前浏览器不支持语音播报')
    return
  }
  const utter = new SpeechSynthesisUtterance(text)
  utter.lang = 'zh-CN'
  window.speechSynthesis.speak(utter)
}
</script>

<style scoped>
.app-container {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 20px;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
}

.left-panel {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  overflow-y: auto;
}

.character-card {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
}
.character-card:hover {
  background: #f5f7fa;
}
.character-avatar {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  background: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.right-panel {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}
.chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  background: #67c23a;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  max-width: 70%;
}

.chat-row.ai {
  flex-direction: row;
}
.chat-row.user {
  flex-direction: row-reverse;
  align-self: flex-end;
}

.bubble {
  padding: 10px 14px;
  border-radius: 12px;
  line-height: 1.4;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.bubble.ai {
  background: #f0f2f5;
}
.bubble.user {
  background: #409eff;
  color: white;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #67c23a;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  color: white;
}

.chat-input {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}
</style>
