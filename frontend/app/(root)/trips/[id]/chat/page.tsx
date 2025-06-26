'use client';

import { useTrip } from '@/context/TripContext';
import { useGroupChat } from '@/context/GroupChatContext';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { ArrowLeft, MessageCircleMore, SendHorizonal } from 'lucide-react';
import { formatDate } from '@/lib/utils';
import { Formik, Form, Field } from 'formik';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import { chatMessageFormSchema } from '@/validation/chatMessageFormSchema';
import { appStore } from '@/store/appStore';
import TextareaAutosize from 'react-textarea-autosize';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import { useState, useRef, useEffect, useCallback } from 'react';
import Link from 'next/link';

export default function ChatPage() {
  const router = useRouter();
  const { trip } = useTrip();
  const { messages, sendMessage, loadMoreMessages, hasMore, isLoading } =
    useGroupChat();
  const user = appStore((s) => s.user);

  const messagesEndRef = useRef<HTMLDivElement>(null);
  const messagesContainerRef = useRef<HTMLDivElement>(null);

  const [isInitialLoad, setIsInitialLoad] = useState(true);
  const prevMessagesLength = useRef(0);
  const MAX_LENGTH = 500;
  const [charCount, setCharCount] = useState(0);

  useEffect(() => {
    const container = messagesContainerRef.current;
    if (!container) return;

    if (isInitialLoad && messages.length > 0) {
      messagesEndRef.current?.scrollIntoView({ behavior: 'auto' });
      setIsInitialLoad(false);
      prevMessagesLength.current = messages.length;
    } else if (messages.length > prevMessagesLength.current) {
      const isNearBottom =
        container.scrollHeight - container.scrollTop <=
        container.clientHeight + 100;

      if (isNearBottom) {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
      }

      prevMessagesLength.current = messages.length;
    }
  }, [messages, isInitialLoad]);

  const handleScroll = useCallback(() => {
    const container = messagesContainerRef.current;
    if (!container || isLoading || !hasMore) return;

    if (container.scrollTop < 100) {
      const prevHeight = container.scrollHeight;

      loadMoreMessages().then(() => {
        requestAnimationFrame(() => {
          setTimeout(() => {
            const newHeight = container.scrollHeight;
            if (messagesContainerRef.current) {
              messagesContainerRef.current.scrollTop = newHeight - prevHeight;
            }
          }, 0);
        });
      });
    }
  }, [isLoading, hasMore, loadMoreMessages]);

  if (!trip) return <div>Loading...</div>;

  return (
    <>
      <div className="flex justify-between gap-3 md:items-center md:flex-row flex-col mb-4">
        <Button
          onClick={() => router.push(`/trips/${trip.id}`)}
          className="bg-white w-min border text-primary-500 hover:bg-gray-100 shadow-sm rounded-full"
        >
          <ArrowLeft />
          <span>Back To Trip Summary</span>
        </Button>
        <div className="text-md font-semibold text-gray-400">
          {trip.name}
          {trip.tripStartDate && trip.tripEndDate && (
            <>
              {' | '}
              {formatDate(trip.tripStartDate)} - {formatDate(trip.tripEndDate)}
            </>
          )}
        </div>
      </div>

      <div className="p-6 flex flex-col gap-4 border bg-white shadow-md rounded-lg">
        <h2 className="text-heading3-bold text-primary-500">{trip.name}</h2>

        <div
          ref={messagesContainerRef}
          onScroll={handleScroll}
          className="flex flex-col gap-3 h-[400px] overflow-y-auto border rounded p-3"
        >
          {isLoading && (
            <div className="text-center text-gray-500 py-2">
              Loading more messages...
            </div>
          )}

          {user && messages.length > 0 ? (
            <>
              {messages.map((msg, i) => {
                const isOwnMessage = msg.senderId === user.id;
                return (
                  <div
                    key={`${msg.id}-${i}`}
                    className={`flex flex-col max-w-[80%] ${
                      isOwnMessage
                        ? 'self-end items-end'
                        : 'self-start items-start'
                    }`}
                  >
                    {!isOwnMessage && (
                      <div className="text-xs text-primary-500/70">
                        <Link href={`/account/${msg.senderId}`}>
                          {msg.senderEmail}
                        </Link>
                      </div>
                    )}
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <div
                          className={`px-4 py-2 rounded-xl text-sm shadow-sm break-all whitespace-pre-wrap ${
                            isOwnMessage
                              ? 'bg-primary-500 text-white rounded-br-none'
                              : 'bg-gray-100 text-gray-800 rounded-bl-none'
                          }`}
                        >
                          {msg.content}
                        </div>
                      </TooltipTrigger>
                      <TooltipContent side={isOwnMessage ? 'left' : 'right'}>
                        <div className="text-sm text-gray-500">
                          {new Date(msg.timestamp).toLocaleString()}
                        </div>
                      </TooltipContent>
                    </Tooltip>
                  </div>
                );
              })}
            </>
          ) : (
            <div className="h-[400px] flex flex-col items-center justify-center gap-3">
              <MessageCircleMore size={200} className="text-gray-200" />
              <p className="text-gray-400 font-semibold text-center">
                No messages yet. Start the conversation!
              </p>
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>

        <Formik
          initialValues={{ message: '' }}
          validationSchema={toFormikValidationSchema(chatMessageFormSchema)}
          onSubmit={(values, { resetForm }) => {
            sendMessage(values.message);
            setCharCount(0);
            resetForm();
          }}
        >
          {({ isSubmitting, isValid, values, setFieldValue }) => (
            <Form className="w-full">
              <label
                htmlFor="message"
                className="font-semibold mb-1 block text-gray-600"
              >
                Message
              </label>
              <div className="flex gap-2 items-end">
                <Field
                  id="message"
                  name="message"
                  as={TextareaAutosize}
                  maxLength={MAX_LENGTH}
                  placeholder="Type your message..."
                  className="flex-1 border rounded px-4 py-2 resize-none max-h-40 overflow-auto focus:outline-none focus:ring focus:border-blue-300"
                  onKeyDown={(e: React.KeyboardEvent<HTMLTextAreaElement>) => {
                    if (e.key === 'Enter' && !e.shiftKey) {
                      e.preventDefault();
                      (e.target as HTMLTextAreaElement).form?.requestSubmit();
                    }
                  }}
                  onInput={(e: React.ChangeEvent<HTMLTextAreaElement>) => {
                    const val = e.target.value.slice(0, MAX_LENGTH);
                    setCharCount(val.length);
                    setFieldValue('message', val);
                  }}
                />
                <button
                  type="submit"
                  className="shadow-sm flex items-center w-min p-2 rounded-full text-primary-500 cursor-pointer hover:bg-gray-100 disabled:text-gray-300 disabled:hover:bg-transparent disabled:cursor-default"
                  disabled={isSubmitting || !isValid || !values.message.trim()}
                >
                  <SendHorizonal width={28} height={28} />
                </button>
              </div>
              <div className="text-xs text-gray-400 mt-1 flex justify-end">
                <div className="flex gap-2">
                  {charCount === 500 && (
                    <div className="text-red-700/60">
                      Max length is 500 characters.
                    </div>
                  )}
                  <div>
                    {charCount} / {MAX_LENGTH}
                  </div>
                </div>
              </div>
            </Form>
          )}
        </Formik>
      </div>
    </>
  );
}
