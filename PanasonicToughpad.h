#ifndef PANASONICTOUGHPAD_H
#define PANASONICTOUGHPAD_H

#include <QObject>
#include <QAndroidJniObject>
#include <QAndroidJniEnvironment>
#include <QtAndroid>

#include "helpers.h" // AUTO_PROPERTY

class PanasonicToughpad : public QObject
{
    Q_OBJECT

    AUTO_PROPERTY(QString, device)
    AUTO_PROPERTY(QString, symbology)
    AUTO_PROPERTY(QString, data)

public:
    explicit PanasonicToughpad(QObject *parent = nullptr);
    static PanasonicToughpad *instance() { return m_instance; }

    Q_INVOKABLE void enableReader();
    Q_INVOKABLE void pressSoftwareTrigger(bool toggle);

signals:
    void barcodeReaded();
    void buttonPressed(const QString &button, const bool state);

public slots:

private:
    static PanasonicToughpad *m_instance;
    jmethodID m_pressSoftwareTriggerMethod;
    jmethodID m_enableReaderMethod;
    jobject m_objectRef;
    QAndroidJniEnvironment m_env;
};

#endif // PANASONICTOUGHPAD_H
