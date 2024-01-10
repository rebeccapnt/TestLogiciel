package services;

import models.AlertData;

public interface IAlertWriter {
    public void writeAlert(AlertData alertData);
}
