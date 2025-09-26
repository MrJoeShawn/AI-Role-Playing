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
        <h3 style="margin: 0">{{ currentCharacter }}</h3>
        <div style="font-size: 12px; color: #999">
          当前模式：{{ replyMode === 'text' ? '文字回复' : '语音回复' }}
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
          <div class="bubble" :class="msg.role">{{ msg.text }}</div>
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
        <el-button type="primary" @click="send" :loading="isLoading">发送</el-button>
        <el-button type="success" @click="startVoice" :disabled="isListening">
          🎤 {{ isListening ? '听中...' : '语音输入' }}
        </el-button>
        <el-button type="danger" @click="stopAll">⏹ 中断</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const query = ref('')
const input = ref('')
const messages = ref([{ role: 'ai', text: '你好，我是哈利波特！你可以和我文字或语音交流。' }])
const currentCharacter = ref('哈利波特')
const isLoading = ref(false)

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

async function send() {
  if (!input.value) return
  const userMsg = input.value
  input.value = ''
  messages.value.push({ role: 'user', text: userMsg })
  scrollToBottom()

  const loadingMsg = { role: 'ai', text: '🤔 正在思考...' }
  messages.value.push(loadingMsg)
  scrollToBottom()
  isLoading.value = true

  try {
    const res = await request.post('/chat', { message: userMsg })
    const reply = res.data
    messages.value[messages.value.length - 1] = { role: 'ai', text: reply }
    scrollToBottom()

    if (replyMode.value === 'voice') {
      speak(reply)
    }
  } catch (error) {
    messages.value[messages.value.length - 1] = { role: 'ai', text: '❌ 出错了，请稍后重试。' }
    ElMessage.error('请求失败，请检查后端服务是否正常')
    console.error(error)
  } finally {
    isLoading.value = false
  }
}

/* --- 语音输入 --- */
let recognition = null
const isListening = ref(false)

if ('webkitSpeechRecognition' in window) {
  recognition = new webkitSpeechRecognition()
  recognition.lang = 'zh-CN'
  recognition.interimResults = false
  recognition.maxAlternatives = 1

  recognition.onstart = () => (isListening.value = true)
  recognition.onend = () => (isListening.value = false)
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

function speak(text) {
  if (!window.speechSynthesis) {
    ElMessage.warning('当前浏览器不支持语音播报')
    return
  }
  const utter = new SpeechSynthesisUtterance(text)
  utter.lang = 'zh-CN'
  window.speechSynthesis.speak(utter)
}

function stopAll() {
  if (recognition && isListening.value) {
    recognition.stop()
    isListening.value = false
  }
  if (window.speechSynthesis && window.speechSynthesis.speaking) {
    window.speechSynthesis.cancel()
  }
  ElMessage.info('已中断当前操作')
}
</script>

<style scoped>
html,
body,
#app {
  height: 100%;
  margin: 0;
  overflow: hidden;
}

.app-container {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 20px;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  overflow: hidden;
}

.left-panel {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  height: 95%; /* 保证左边满高 */
  overflow-y: auto;
}

.right-panel {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  height: 95%; /* 保证右边满高 */
  overflow: hidden; /* 🚀 关键：禁止整体滚动 */
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

.chat-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.chat-messages {
  flex: 1;
  overflow-y: auto; /* ✅ 单独滚动 */
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-row {
  display: flex;
  max-width: 70%;
}

.chat-row.ai {
  justify-content: flex-start;
}
.chat-row.user {
  align-self: flex-end;
  justify-content: flex-end;
}

.bubble {
  padding: 10px 14px;
  border-radius: 12px;
  line-height: 1.4;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  word-break: break-word;
}

.bubble.ai {
  background: #f0f2f5;
}
.bubble.user {
  background: #409eff;
  color: white;
}

.chat-input {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  flex-shrink: 0;
}
</style>
